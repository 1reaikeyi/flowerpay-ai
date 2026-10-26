package start.statistics;

import com.branch.result.Result;
import framework.aop.oparation.OperationSuccessEnum;
import start.statistics.vo.OrderStatisticsVO;
import start.statistics.vo.StatisticsVO;
import start.statistics.vo.TopStatisticsVO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import framework.aop.OperationLogging;

import java.util.*;

@RestController
@RequestMapping("admin/statistics")
public class Statistics {

    @Autowired
    private StatisticsService statisticsService;

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/flower")
    public Result flowerSale() {
        List<StatisticsVO> statisticsVOList = statisticsService.flowerSale();
        return Result.success(statisticsVOList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/festival")
    public Result festivalSale() {
        List<StatisticsVO> festivalStatisticsVOList = statisticsService.festivalSale();
        return Result.success(festivalStatisticsVOList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/top1")
    public Result top1() {
        List<TopStatisticsVO> statisticsVOList = statisticsService.top1();
        return Result.success(statisticsVOList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/top2")
    public Result top2() {
        List<TopStatisticsVO> festivalStatisticsVOList = statisticsService.top2();
        return Result.success(festivalStatisticsVOList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationSuccessEnum.READ)
    @GetMapping("/today")
    public Result today() {
        List<OrderStatisticsVO> orderStatisticsVOList = statisticsService.todayOrder();
        return Result.success(orderStatisticsVOList);
    }
}
