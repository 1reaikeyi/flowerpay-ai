package com.branch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.model.dto.TimeDTO;
import com.branch.model.entity.Sign;


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

    Object countOfMonth();

    Object countOfTime(TimeDTO time);

}
