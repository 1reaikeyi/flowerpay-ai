package com.branch.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 优惠券表
 * </p>
 *
 * @author flower
 * @since 2026-10-29
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("voucher")
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属门店ID
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 优惠券标题
     */
    @TableField("title")
    private String title;

    /**
     * 优惠券副标题
     */
    @TableField("sub_title")
    private String subTitle;

    /**
     * 券类型：0普通代金券，1秒杀券
     */
    @TableField("type")
    private Long type;

    /**
     * 优惠券状态：1上架，2下架，3过期
     */
    @TableField("status")
    private Long status;

    /**
     * 优惠券使用规则
     */
    @TableField("rules")
    private String rules;

    /**
     * 用户购买支付金额，单位分
     */
    @TableField("pay_value")
    private Long payValue;

    /**
     * 优惠券抵扣金额，单位分
     */
    @TableField("actual_value")
    private Long actualValue;

    /**
     * 优惠券创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 优惠券更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
