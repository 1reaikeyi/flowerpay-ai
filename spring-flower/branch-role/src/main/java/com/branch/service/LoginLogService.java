package com.branch.service;

import com.branch.domain.entity.LoginLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.result.PagePart;

import java.util.List;

/**
 * <p>
 * 系统访问记录 服务类
 * </p>
 *
 * @author flower
 * @since 2026-10-25
 */
public interface LoginLogService extends IService<LoginLog> {

    PagePart<LoginLog> read();

    void deleteRole(List<Long> ids);
}
