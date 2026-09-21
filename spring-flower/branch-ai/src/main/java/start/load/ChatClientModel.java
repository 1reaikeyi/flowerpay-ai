package start.load;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.JedisPooled;
import service.tool.FlowerTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientModel {
    @Bean
    public ChatClient chatClient(OpenAiChatModel model,
                                 @Qualifier("loggerAdvisor") Advisor loggerAdvisor,
                                 @Qualifier("memoryAdvisor") Advisor messageMemoryAdvisor
                                ) {  // 日志记录器)
        return ChatClient.builder(model)
                .defaultAdvisors(loggerAdvisor, messageMemoryAdvisor)
                .build();
    }
    @Bean
    public ChatClient toolClient(OpenAiChatModel model,
                                 @Qualifier("loggerAdvisor") Advisor loggerAdvisor,
                                 @Qualifier("memoryAdvisor") Advisor messageMemoryAdvisor,
                                 FlowerTool flowerTool) {  // 日志记录器)
        return ChatClient.builder(model)
                .defaultAdvisors(loggerAdvisor, messageMemoryAdvisor)
                .defaultTools(flowerTool)
                .build();
    }
    @Bean
    public ChatClient visualChatClient(OpenAiChatModel model,
                                       @Qualifier("loggerAdvisor") Advisor loggerAdvisor) {  // 日志记录器
        return ChatClient.builder(model)
                .defaultAdvisors(loggerAdvisor)
                .build();
    }
    @Value("${spring.data.redis.password}")
    private String auth;
    @Value("${spring.data.redis.port:16379}")
    private int redisPort;
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Bean
    public JedisPooled jedisPooled() {
        // 显式指定 database 0：RediSearch 向量索引仅支持 database 0
        return new JedisPooled("redis://:"+auth+"@"+redisHost+":"+redisPort+"/0");
    }

    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("spring-ai-index")
                .prefix("emb:")
                .initializeSchema(true)
                .build();
    }
}
