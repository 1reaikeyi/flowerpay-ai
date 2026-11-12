import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class MapTest {
    @Test
    public void test() {

        String key = System.getenv("PATH_gaodeditu_key");
        System.out.println(key);
       String ip = "124.16.80.110"; //北大官网公网IP
       String address = "北京市海淀区北京大学";
        String location = "116.403874,39.914885";
        String url = "https://restapi.amap.com/v3/ip?ip="+ip+"&key="+key;
        String url2 = "https://restapi.amap.com/v3/geocode/geo?address="+address+"&key="+key;
        String url3 = "https://restapi.amap.com/v3/geocode/regeo?location="+location + "&key="+key;

        RestTemplate restTemplate = new RestTemplate();
//        Map<String,Object> result = restTemplate.getForObject(url, HashMap.class);
        Map<String,Object> result2 = restTemplate.getForObject(url2, HashMap.class);
//        Map<String,Object> result3 = restTemplate.getForObject(url3, HashMap.class);
        System.out.println(result2);
    }
}
