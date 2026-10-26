package service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.dto.VoucherDTO;
import domain.entity.Voucher;
import domain.entity.VoucherSecond;
import lombok.extern.slf4j.Slf4j;
import mapper.VoucherMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import service.VoucherSeckillService;
import service.VoucherService;

import java.util.List;

/**
 * 优惠券服务实现类 - 实现优惠券相关业务逻辑
 */
@Service
@Slf4j
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    private static final String VOUCHER_STOCK_PREFIX = "voucherSeckill:stock:";

    @Autowired
    private VoucherSeckillService voucherSeckillService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void createVoucher(VoucherDTO voucherDTO) {
        Voucher voucher = BeanUtil.toBean(voucherDTO, Voucher.class);
        super.save(voucher);
        List<VoucherSecond> voucherSecondList = voucherDTO.getVoucherSecondList().stream()
                .map((VoucherSecond voucherSeckill) -> VoucherSecond.builder()
                        .stock(voucherSeckill.getStock())
                        .beginTime(voucherSeckill.getBeginTime())
                        .endTime(voucherSeckill.getEndTime())
                        .voucherId(voucherDTO.getId())
                        .build())
                .toList();
        for (VoucherSecond seckill : voucherSecondList) {
            stringRedisTemplate.opsForValue().set(VOUCHER_STOCK_PREFIX + voucher.getId(),
                    seckill.getStock().toString());
        }
        voucherSeckillService.saveBatch(voucherSecondList);
    }
}
