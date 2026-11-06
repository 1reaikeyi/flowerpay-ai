package com.branch.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 秒杀优惠券表
 * </p>
 *
 * @author flower
 * @since 2026-10-29
 */
@Getter
@Setter
@TableName("voucher_second")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherSecond implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联优惠券表主键ID
     */
    @TableId(value = "voucher_id", type = IdType.AUTO)
    private Long voucherId;

    /**
     * 秒杀库存数量
     */
    @TableField("stock")
    private Long stock;

    /**
     * 秒杀活动开始时间
     */
    @TableField("begin_time")
    private LocalDateTime beginTime;

    /**
     * 秒杀活动结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 记录创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
