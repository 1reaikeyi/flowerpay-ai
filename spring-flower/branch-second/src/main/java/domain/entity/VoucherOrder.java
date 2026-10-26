package domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 优惠券订单实体类 - 对应数据库 voucher_order 表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("voucher_order")
@Builder
public class VoucherOrder implements Serializable {

    /**
     * 订单唯一主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
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
     * 支付方式：1余额，2支付宝，3微信
     */
    @TableField("pay_type")
    private Long payType;

    /**
     * 订单状态
     */
    @TableField("status")
    private Long status;

    /**
     * 下单时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 支付完成时间
     */
    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 门店核销使用时间
     */
    @TableField("use_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useTime;

    /**
     * 退款完成时间
     */
    @TableField("refund_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    /**
     * 订单更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
