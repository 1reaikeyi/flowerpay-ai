package start.websocket;

import com.branch.domain.entity.Flower;
import com.branch.service.FlowerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private FlowerService flowerService;
    /**
     *   @Scheduled 使用 6 字段 cron 表达式（秒 分 时 日 月 周）
     *   1	秒	0-59	每隔 n 秒
     *   2	分	0-59	每隔 n 分钟
     *   3	时	0-23	任意小时
     *   4	日	1-31	任意日期
     *   5	月	1-12	任意月份
     *   6	周	1 (周日)-7 (周六)	? 不指定（和日互斥）
     */

    @Scheduled(cron = "0 0 8 * * ?")
    public void processTimeout(){
        List<Flower> flowerList = flowerService
                .lambdaQuery()
                .orderByAsc(Flower::getUpdateTime)
                .list();
        LocalDateTime now = LocalDateTime.now();
        for (Flower flower : flowerList) {
            if (flower.getUpdateTime().plusHours(24).isBefore(now)) {
                String msg = "鲜花[" + flower.getId() + "]保质期超过24小时，请及时处理";
                log.info(msg);
                WebSocketServer.sendToAdmin("1", msg);
            }
        }
    }
}
