package com.branch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.service.*;
import com.branch.result.PageResult;


import com.branch.framework.zhifubao.PayDTO;
import com.branch.framework.zhifubao.RefundDTO;
import com.branch.framework.zhifubao.service.ZhifubaoService;
import jakarta.servlet.http.HttpServletResponse;
import com.branch.mapper.FlowerOrderMapper;
import com.branch.domain.dto.FlowerOrderPageDTO;
import com.branch.domain.entity.FlowerOrder;
import com.branch.domain.entity.FlowerOrderDetail;
import com.branch.domain.entity.FlowerOrderPay;
import com.branch.domain.enums.OrderStatusEnum;
import com.branch.domain.enums.PayStatusEnum;
import com.branch.domain.vo.FlowerOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlowerOrderServiceImpl extends ServiceImpl<FlowerOrderMapper, FlowerOrder> implements FlowerOrderService {

    @Autowired
    private ZhifubaoService zhifubaoService;
    @Autowired
    private FlowerOrderPayService flowerOrderPayService;
    @Autowired
    private FlowerOrderDetailService flowerOrderDetailService;


    @Override
    public FlowerOrderVO readById(Long id) {
        FlowerOrder flowerOrder = this.getById(id);
        FlowerOrderVO flowerOrderVO = BeanUtil.toBean(flowerOrder, FlowerOrderVO.class);
        return flowerOrderVO;
    }

    @Override
    public PageResult<FlowerOrderVO> readPage(FlowerOrderPageDTO flowerOrderPageDTO) {
        LambdaQueryWrapper<FlowerOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(flowerOrderPageDTO.getStatus() != null,
                FlowerOrder::getStatus, flowerOrderPageDTO.getStatus());
        IPage page = new Page(flowerOrderPageDTO.getPage(),flowerOrderPageDTO.getPageSize());
        IPage<FlowerOrder> flowerOrderIPage= this.page(page,queryWrapper);
        List<FlowerOrderVO> voList = flowerOrderIPage.getRecords().stream()
                .map(flowerOrder -> BeanUtil.copyProperties(flowerOrder, FlowerOrderVO.class))
                .collect(Collectors.toList());

        PageResult<FlowerOrderVO> result = new PageResult<>();
        result.setTotal(flowerOrderIPage.getTotal());
        result.setList(voList);                         // 当前页数据
        result.setPageNum(flowerOrderIPage.getCurrent());  // 当前页码
        result.setPageSize(flowerOrderIPage.getSize());    // 每页条数
        return result;
    }

    @Override
    public void update3(Long id) {
        this.lambdaUpdate()
                .eq(FlowerOrder::getId, id)
                .set(FlowerOrder::getStatus, OrderStatusEnum.COOKING);
    }

    @Override
    public void update4(Long id) {
        this.lambdaUpdate()
                .eq(FlowerOrder::getId, id)
                .set(FlowerOrder::getStatus, OrderStatusEnum.GO);
    }

    @Override
    public void update5(Long id) {
        this.lambdaUpdate()
                .eq(FlowerOrder::getId, id)
                .set(FlowerOrder::getStatus, OrderStatusEnum.DELIVERING);
    }

    @Override
    public void update6(Long id) {
        this.lambdaUpdate()
                .eq(FlowerOrder::getId, id)
                .set(FlowerOrder::getStatus, OrderStatusEnum.ARRIVED);
    }

    @Override
    public void update7(Long id) {
        this.lambdaUpdate()
                .eq(FlowerOrder::getId, id)
                .set(FlowerOrder::getStatus, OrderStatusEnum.COMPLETED);
    }

    @Override
    public void update8(Long id) {
        FlowerOrder flowerOrder = this.getById(id);

        FlowerOrderPay flowerOrderPay = flowerOrderPayService.lambdaQuery()
                .eq(FlowerOrderPay::getOrderId, id)
                .one();
        RefundDTO refundDTO = new RefundDTO();
        refundDTO.setOutTradeNo(flowerOrderPay.getId().toString());
        refundDTO.setOutRefundNo(flowerOrderPay.getId().toString());
        refundDTO.setRefundReason("已退款");
        refundDTO.setRefundAmount(flowerOrderPay.getAmount());
        try {
            refund(refundDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        flowerOrderPayService.lambdaUpdate()
                .eq(FlowerOrderPay::getOrderId, flowerOrder.getId())
                .set(FlowerOrderPay::getPayStatus, PayStatusEnum.REFUNDED);

        this.lambdaUpdate()
                .eq(FlowerOrder::getId, id)
                .set(FlowerOrder::getStatus, OrderStatusEnum.CANCELLED);
    }

    @Override
    public void update1(Long id) {
        this.lambdaUpdate()
                .eq(FlowerOrder::getId, id)
                .set(FlowerOrder::getStatus, OrderStatusEnum.COOKING);

        FlowerOrderDetail flowerOrderDetail = flowerOrderDetailService
                .lambdaQuery()
                .eq(FlowerOrderDetail::getOrderId, id).one();
        FlowerOrderPay flowerOrderPay = FlowerOrderPay.builder()
                .orderId(id)
                .orderTime(LocalDateTime.now())
                .amount(flowerOrderDetail.getAmount().add(BigDecimal.valueOf(flowerOrderDetail.getWrapFee())))
                .build();
        flowerOrderPayService.save(flowerOrderPay);
    }

    @Override
    public void update2(Long id,HttpServletResponse httpServletResponse) {
        this.lambdaUpdate()
                .eq(FlowerOrder::getId, id)
                .set(FlowerOrder::getStatus, OrderStatusEnum.COOKING);

        FlowerOrderPay flowerOrderPay = flowerOrderPayService.lambdaQuery()
                .eq(FlowerOrderPay::getOrderId, id)
                .one();
        PayDTO payDTO = new PayDTO();
        payDTO.setOutTradeNo(flowerOrderPay.getId().toString());
        payDTO.setSubject(flowerOrderPay.getId().toString());
        payDTO.setTotalAmount(flowerOrderPay.getAmount());
        try {
            order(payDTO,httpServletResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void order(PayDTO payDTO, HttpServletResponse response) throws Exception {
        String form = zhifubaoService.createPagePayForm(payDTO);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(form);
        response.getWriter().flush();
    }
    public AlipayTradeRefundResponse refund(RefundDTO refundDTO) throws Exception {
        return zhifubaoService.refund(refundDTO);
    }
}
