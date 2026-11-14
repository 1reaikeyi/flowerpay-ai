package com.branch.controller;

import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.branch.domain.dto.FlowerOrderPageDTO;
import com.branch.domain.enums.OrderStatusEnum;
import com.branch.domain.vo.FlowerOrderVO;
import com.branch.service.FlowerOrderService;
import com.branch.domain.OperationSuccessEnum;
import com.branch.result.PageResult;
import com.branch.result.Result;
import com.branch.framework.zhifubao.PayDTO;
import com.branch.framework.zhifubao.RefundDTO;
import com.branch.framework.zhifubao.service.ZhifubaoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.branch.framework.aop.OperationLogging;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/admin/flowerOrder")
@Slf4j
public class AdminFlowerOrderController {
    @Autowired
    private FlowerOrderService flowerOrderService;
    @Autowired
    private ZhifubaoService zhifubaoService;

    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result readById(@RequestParam("id") Long id) {
        FlowerOrderVO flowerOrderVO = flowerOrderService.readById(id);
        return Result.success(flowerOrderVO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/all")
    public Result readPage(FlowerOrderPageDTO flowerOrderPageDTO) {
        PageResult<FlowerOrderVO> flowerOrderVOPageResult = flowerOrderService.readPage(flowerOrderPageDTO);
        return Result.success(flowerOrderVOPageResult);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("cooking/{id}")
    public Result update3(@PathVariable Long id) {
        flowerOrderService.update3(id);
        return Result.success(OrderStatusEnum.COOKING);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("go/{id}")
    public Result update4(@PathVariable Long id) {
        flowerOrderService.update4(id);
        return Result.success(OrderStatusEnum.GO);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("delivering/{id}")
    public Result update5(@PathVariable Long id) {
        flowerOrderService.update5(id);
        return Result.success(OrderStatusEnum.DELIVERING);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("arrived/{id}")
    public Result update6(@PathVariable Long id) {
        flowerOrderService.update6(id);
        return Result.success(OrderStatusEnum.ARRIVED);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("/complete/{id}")
    public Result update7(@PathVariable Long id) {
        flowerOrderService.update7(id);
        return Result.success(OrderStatusEnum.COMPLETED);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.UPDATE)
    @PutMapping("canceled/{id}")
    public Result update8(@PathVariable Long id) {
        flowerOrderService.update8(id);
        return Result.success(OrderStatusEnum.CANCELLED);
    }

    @GetMapping("/order")
    public void orderPay(PayDTO payDTO, HttpServletResponse response) throws Exception {
        String form = zhifubaoService.createPagePayForm(payDTO);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(form);
        response.getWriter().flush();
    }

    /** 交易查询 */
    @GetMapping("/order/query")
    public AlipayTradeQueryResponse queryOrder(@RequestParam String outTradeNo) throws Exception {
        return zhifubaoService.queryTrade(outTradeNo);
    }

    /** 退款 */
    @PostMapping("/refund")
    public AlipayTradeRefundResponse refundOrder(RefundDTO refundDTO) throws Exception {
        return zhifubaoService.refund(refundDTO);
    }

    /** 退款查询 */
    @GetMapping("/refund/query")
    public AlipayTradeFastpayRefundQueryResponse refundQuery(@RequestParam String outTradeNo, @RequestParam String outRequestNo) throws Exception {
        return zhifubaoService.refundQuery(outTradeNo, outRequestNo);
    }

    /** 关闭交易 */
    @PostMapping("/order/close")
    public AlipayTradeCloseResponse close(@RequestParam String outTradeNo) throws Exception {
        return zhifubaoService.close(outTradeNo);
    }
}
