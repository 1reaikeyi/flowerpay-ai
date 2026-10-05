package start.controller.statistics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodayStatisticsVO {
    private String count;
    private LocalDateTime localDateTime;
}
