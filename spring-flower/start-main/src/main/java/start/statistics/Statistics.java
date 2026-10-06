package start.statistics;

import start.statistics.StatisticsService;
import start.statistics.vo.OrderStatisticsVO;
import start.statistics.vo.StatisticsVO;
import start.statistics.vo.TopStatisticsVO;
import framework.aop.oparation.OperationEnum;
import common.result.Result;

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
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/flower")
    public Result flowerSale() {
        List<StatisticsVO> statisticsVOList = statisticsService.flowerSale();
        return Result.success(statisticsVOList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/festival")
    public Result festivalSale() {
        List<StatisticsVO> festivalStatisticsVOList = statisticsService.festivalSale();
        return Result.success(festivalStatisticsVOList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/top1")
    public Result top1() {
        List<TopStatisticsVO> statisticsVOList = statisticsService.top1();
        return Result.success(statisticsVOList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/top2")
    public Result top2() {
        List<TopStatisticsVO> festivalStatisticsVOList = statisticsService.top2();
        return Result.success(festivalStatisticsVOList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/order")
    public Result order() {
        List<OrderStatisticsVO> orderStatisticsVOList = statisticsService.order();
        return Result.success(orderStatisticsVOList);
    }

    // 今日统计：今日订单数、已支付订单数、今日营业额
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLogging(operation = OperationEnum.READ)
    @GetMapping("/today")
    public Result today() {
        List<OrderStatisticsVO> orderStatisticsVOList = statisticsService.todayOrder();
        return Result.success(orderStatisticsVOList);
    }
}
