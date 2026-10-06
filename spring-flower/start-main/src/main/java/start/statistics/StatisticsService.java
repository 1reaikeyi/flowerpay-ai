package start.statistics;

import com.branch.domain.vo.FestivalVO;
import com.branch.domain.vo.FlowerVO;
import com.branch.service.FestivalService;
import com.branch.service.FlowerService;
import start.statistics.vo.OrderStatisticsVO;
import com.branch.domain.entity.FlowerOrder;
import com.branch.domain.entity.FlowerOrderDetail;
import com.branch.domain.enums.OrderStatusEnum;

import com.branch.service.FlowerOrderDetailService;
import com.branch.service.FlowerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import start.statistics.vo.StatisticsVO;
import start.statistics.vo.TopStatisticsVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private FlowerService flowerService;
    @Autowired
    private FestivalService festivalService;
    @Autowired
    private FlowerOrderDetailService flowerOrderDetailService;
    @Autowired
    private FlowerOrderService flowerOrderService;

    private static final int TOP_NUMBER = 4;

    public List<StatisticsVO> flowerSale() {
        List<FlowerOrderDetail> details = flowerOrderDetailService.lambdaQuery()
                .isNotNull(FlowerOrderDetail::getFlowerId)
                .list();
        List<StatisticsVO> statisticsVOList = new ArrayList<>();
        Map<Long, Long> numberMap = new HashMap<>();
        Map<Long, BigDecimal> amountMap = new HashMap<>();
        for (FlowerOrderDetail d : details) {
            numberMap.merge(d.getFlowerId(), d.getNumber(), Long::sum);
            amountMap.merge(d.getFlowerId(), d.getAmount(), BigDecimal::add);
        }
        // 组装结果
        numberMap.forEach((id, number) -> {
            StatisticsVO statisticsVO = new StatisticsVO();
            FlowerVO flowerVO = flowerService.readCache(id);
            statisticsVO.setName(flowerVO != null ? flowerVO.getName() : null);
            statisticsVO.setId(flowerVO != null ? flowerVO.getId() : null);
            statisticsVO.setCount(number);
            statisticsVO.setTotalAccount(amountMap.getOrDefault(flowerVO, BigDecimal.ZERO));
            statisticsVOList.add(statisticsVO);
        });
        statisticsVOList.sort((a, b) -> Long.compare(b.getCount(),a.getCount()));
        return statisticsVOList;
    }

    public List<StatisticsVO> festivalSale() {
        List<FlowerOrderDetail> details = flowerOrderDetailService.lambdaQuery()
                .isNotNull(FlowerOrderDetail::getFestivalId)
                .list();
        Map<Long, Long> numberMap = new HashMap<>();
        Map<Long, BigDecimal> amountMap = new HashMap<>();
        for (FlowerOrderDetail d : details) {
            numberMap.merge(d.getFestivalId(), d.getNumber(), Long::sum);
            amountMap.merge(d.getFestivalId(), d.getAmount(), BigDecimal::add);
        }
        List<StatisticsVO> statisticsVOList = new ArrayList<>();
        numberMap.forEach((id, number) -> {
            StatisticsVO statisticsVO = new StatisticsVO();
            FestivalVO festivalVO = festivalService.readCache(id);
            statisticsVO.setName(festivalVO != null ? festivalVO.getName() : null);
            statisticsVO.setId(festivalVO != null ? festivalVO.getId() : null);
            statisticsVO.setCount(number);
            statisticsVO.setTotalAccount(amountMap.getOrDefault(festivalVO, BigDecimal.ZERO));
            statisticsVOList.add(statisticsVO);
        });
        statisticsVOList.sort((a, b) -> Long.compare(b.getCount(),a.getCount()));
        return statisticsVOList;
    }

    public List<TopStatisticsVO> top1() {
        List<FlowerOrderDetail> details = flowerOrderDetailService.lambdaQuery()
                .isNotNull(FlowerOrderDetail::getFlowerId)
                .last(" limit " + TOP_NUMBER)
                .list();

        Map<Long, Long> numberMap = new HashMap<>();
        for (FlowerOrderDetail d : details) {
            numberMap.merge(d.getFlowerId(), d.getNumber(), Long::sum);
        }
        // 降序排序取前
        List<TopStatisticsVO> topStatisticsVOList = new ArrayList<>();

        return null;
    }

    public List<TopStatisticsVO> top2() {
        List<FlowerOrderDetail> details = flowerOrderDetailService.lambdaQuery()
                .isNotNull(FlowerOrderDetail::getFestivalId)
                .last(" limit " + TOP_NUMBER)
                .list();

        Map<Long, Long> numberMap = new HashMap<>();
        for (FlowerOrderDetail d : details) {
            numberMap.merge(d.getFlowerId(), d.getNumber(), Long::sum);
        }
        // 降序排序取前
        List<TopStatisticsVO> topStatisticsVOList = new ArrayList<>();
        return List.of();
    }

    public List<OrderStatisticsVO> order() {
        List<FlowerOrder> orders = flowerOrderService.list();
        // 按状态分组计数
        Map<OrderStatusEnum, Long> countMap = orders.stream()
                .collect(Collectors.groupingBy(FlowerOrder::getStatus, Collectors.counting()));
        // 遍历所有枚举值，保证每种状态都返回（数量为 0 也展示）
        List<OrderStatisticsVO> orderStatisticsVOList = new ArrayList<>();
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            OrderStatisticsVO orderStatistics = new OrderStatisticsVO();
            orderStatistics.setStatus(status.getCode());
            orderStatistics.setName(status.getFullText());
            orderStatistics.setCount(countMap.getOrDefault(status, 0L));
            orderStatisticsVOList.add(orderStatistics);
        }
        // 按数量降序排序（修复：原比较 b 与 b 永远相等，无法排序）
        orderStatisticsVOList.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return null;
    }

    public List<OrderStatisticsVO> todayOrder() {
        // 今日 0 点与明天 0 点作为时间区间
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        // 今日订单（按创建时间过滤）
        List<FlowerOrderDetail> orders = flowerOrderDetailService.lambdaQuery()
                .ge(FlowerOrderDetail::getCreateTime, start)
                .lt(FlowerOrderDetail::getCreateTime, end)
                .list();
        // 通过今日订单的 id 关联查询已支付的支付记录，统计实收金额

        return null;
    }
}
