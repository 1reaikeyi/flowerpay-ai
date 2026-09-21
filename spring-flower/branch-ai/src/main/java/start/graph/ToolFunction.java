package start.graph;

import service.tool.ToolService;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
public class ToolFunction implements NodeAction {

    @Autowired
    private ToolService toolService;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Object visualValue = state.value("visualResult").orElse(null);

        String input = visualValue == null ? "图片出错" : visualValue.toString();
        String question = visualValue == null ? "查询相关信息" : visualValue.toString();

        String result =  toolService.chat(input,question)
                .collectList()
                .timeout(Duration.ofSeconds(30))
                .blockOptional(Duration.ofSeconds(35))
                .toString();
        return Map.of("toolResult", result);
    }

}
