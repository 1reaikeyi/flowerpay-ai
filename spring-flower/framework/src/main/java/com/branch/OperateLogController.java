package com.branch;

import com.branch.domain.entity.OperateLog;
import com.branch.domain.vo.OperateLogVO;
import com.branch.result.PagePart;
import com.branch.result.Result;
import com.branch.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 操作日志记录 前端控制器
 * </p>
 *
 * @author flower
 * @since 2026-10-25
 */
@RestController
@RequestMapping("/operate/log")
public class OperateLogController {

    @Autowired
    private OperateLogService operateLogService;
    @GetMapping
    public Result read(@RequestBody OperateLog operateLog) {
        PagePart<OperateLogVO> operateLogVOPagePart = operateLogService.readPage(operateLog);
        return Result.success(operateLogVOPagePart);
    }
    @DeleteMapping
    public Result delete(List<Long> ids) {
        operateLogService.deleteByIds(ids);
        return Result.success(ids);
    }
}
