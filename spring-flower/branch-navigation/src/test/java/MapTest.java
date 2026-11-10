import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class MapTest {
    @Test
    public void test() {

        String key = System.getenv("PATH_gaodeditu_key");
        String ip = "124.16.80.110"; //北大官网公网IP
        String url = "https://restapi.amap.com/v3/ip?ip="+ip+"&key="+key;

        RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> result = restTemplate.getForObject(url, HashMap.class);
        System.out.println(result);
    }

}
