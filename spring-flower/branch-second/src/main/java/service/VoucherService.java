package service;


import com.baomidou.mybatisplus.extension.service.IService;
import domain.dto.VoucherDTO;
import domain.entity.Voucher;

/**
 * 优惠券服务接口 - 定义优惠券相关业务操作
 */
public interface VoucherService extends IService<Voucher> {

    /**
     * 创建优惠券：保存优惠券与秒杀信息，并将库存初始化到 Redis
     */
    void createVoucher(VoucherDTO voucherDTO);
}
