package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.domain.dto.TimeDTO;
import com.branch.domain.entity.Sign;


public interface SignService extends IService<Sign> {

    /**
     * 当日签到（基于 Redis bitmap）
     *
     * @return 结果描述
     */
    String sign();

    /**
     * 补签指定日期（基于 Redis bitmap）
     */
    String backSign(TimeDTO time);

    /**
     * 统计当月截至今日的签到/缺勤天数
     *
     * @return 结果描述；无数据时返回数字 0
     */
    Object countOfMonth();

    /**
     * 统计指定月份的签到情况，并落库保存到 Sign 表
     *
     * @return 保存的 Sign 记录；无数据时返回数字 0
     */
    Object countOfTime(TimeDTO time);

}
