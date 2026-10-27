package com.branch.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUPerDTO {

    /**
     * 主键，用户 id
     */
    private Long userId;

    /**
     * 城市名称
     */

    private String city;

    /**
     * 个人介绍，不要超过 128 个字符
     */

    private String introduce;

    /**
     * 粉丝数量
     */

    private Long fans;

    /**
     * 关注的人的数量
     */

    private Long followee;

    /**
     * 性别，0：男，1：女
     */

    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 积分
     */

    private Long credits;

    /**
     * 会员级别，0~9级，0代表未开通会员
     */

    private Integer level;
}
