package com.branch.service;

import com.branch.model.entity.OperateLog;
import com.branch.model.vo.OperateLogVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.branch.result.PagePart;

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

    PagePart<OperateLogVO> readPage(OperateLog operateLog);
}
