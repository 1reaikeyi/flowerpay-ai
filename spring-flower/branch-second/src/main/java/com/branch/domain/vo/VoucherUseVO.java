package com.branch.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class VoucherUseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单唯一主键ID
     */

    private Long id;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 购买的优惠券ID
     */
    private Long voucherId;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;
}
