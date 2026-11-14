package com.branch.service;

import com.branch.domain.entity.OperateLog;
import com.branch.domain.vo.OperateLogVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.result.PageResult;

import java.util.List;

/**
 * <p>
 * 操作日志记录 服务类
 * </p>
 *
 * @author flower
 * @since 2026-10-25
 */
public interface OperateLogService extends IService<OperateLog> {

    void deleteByIds(List<Long> ids);

    PageResult<OperateLogVO> readPage(OperateLog operateLog);
}
