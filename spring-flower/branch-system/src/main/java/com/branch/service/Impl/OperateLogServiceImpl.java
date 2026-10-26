package com.branch.service.Impl;

import com.branch.domain.entity.OperateLog;
import com.branch.mapper.OperateLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.branch.service.OperateLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志记录 服务实现类
 * </p>
 *
 * @author flower
 * @since 2026-10-25
 */
@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements OperateLogService {

}
