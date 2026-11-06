package com.branch.domain.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class VoucherVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 优惠券主键ID
     */
    private Long id;

    /**
     * 所属门店ID
     */
    private Long shopId;

    /**
     * 优惠券标题
     */
    private String title;

    /**
     * 优惠券副标题
     */
    private String subTitle;

    /**
     * 券类型：0普通代金券，1秒杀券
     */
    private Long type;

    /**
     * 优惠券状态：1上架，2下架，3过期
     */
    private Long status;

    /**
     * 优惠券使用规则
     */
    private String rules;

    /**
     * 用户购买支付金额，单位分
     */
    private Long payValue;

    /**
     * 优惠券抵扣金额，单位分
     */
    private Long actualValue;

}
