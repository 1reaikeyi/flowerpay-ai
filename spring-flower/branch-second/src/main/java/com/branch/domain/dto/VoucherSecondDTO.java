package com.branch.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class VoucherSecondDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关联优惠券表主键ID
     */
    private Long voucherId;

    /**
     * 秒杀库存数量
     */
    private Long stock;

    /**
     * 秒杀活动开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 秒杀活动结束时间
     */
    private LocalDateTime endTime;

}
