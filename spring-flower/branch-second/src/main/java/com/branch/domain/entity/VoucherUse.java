package com.branch.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 购买订单表
 * </p>
 *
 * @author flower
 * @since 2026-10-29
 */
@Getter
@Setter
@TableName("voucher_use")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单唯一主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 下单用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 购买的优惠券ID
     */
    @TableField("voucher_id")
    private Long voucherId;

    /**
     * 使用时间
     */
    @TableField("use_time")
    private LocalDateTime useTime;

}
