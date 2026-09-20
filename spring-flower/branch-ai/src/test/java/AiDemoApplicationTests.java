import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import service.rag.Chat;
import service.rag.VectorDistanceUtils;
import start.AIApplication;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SpringBootTest(classes = AIApplication.class)
public class AiDemoApplicationTests {

    @Autowired
    private OpenAiEmbeddingModel embeddingModel;
    @Autowired
    private VectorStore vectorStore;  // 注入你配置的向量库
    @Autowired
    private Chat chatService;
    /**
     * 加入数据
     */
    @Test
    public void add() {
        Document doc1 = Document.builder()
                .text("玫瑰是蔷薇科蔷薇属植物，被誉为\"花中皇后\"，" +
                        "花色丰富，有红、粉、白、黄等多种颜色，" +
                        "象征爱情与美好，是情人节最受欢迎的鲜花之一。")
                .metadata(Map.of("type", "flower", "source", "知识库"))
                .id("flower-rose")
                .build();

        Document doc2 = Document.builder()
                .text("百合是百合科百合属多年生草本植物，花朵硕大优雅，" +
                        "常见有白色、粉色、黄色等，香味浓郁清新，" +
                        "寓意百年好合、纯洁高雅，常用于婚礼和家居装饰。")
                .metadata(Map.of("type", "flower", "source", "知识库"))
                .id("flower-lily")
                .build();

        Document doc3 = Document.builder()
                .text("向日葵是菊科向日葵属植物，花盘形似太阳，朝向阳光生长，" +
                        "花色以金黄色为主，花语是沉默的爱、仰慕与忠诚，" +
                        "适合送给朋友和长辈，传递积极向上的正能量。")
                .metadata(Map.of("type", "flower", "source", "知识库"))
                .id("flower-sunflower")
                .build();

        // 2. 长文本切分（可选，短文本可直接 add）
        List<Document> documents = new TokenTextSplitter()
                .apply(List.of(doc1, doc2, doc3));

        // 3. 写入向量库，内部自动调用 embeddingModel 生成向量
        vectorStore.add(documents);

        System.out.println("数据添加完成，共 " + documents.size() + " 个文档片段");
    }

    @Test
    public void get() {
        String question = "介绍向日葵？";

        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(3)
                // .similarityThreshold(0.5)   // 0~1，越大越严格
                 .filterExpression("type == 'flower'")  // metadata 过滤，可选
                .build();
        List<Document> retrievedDocs = vectorStore.similaritySearch(searchRequest);
        System.out.println("===== 召回的文档 =====");
        for (Document doc : retrievedDocs) {
            Number score = (Number) doc.getMetadata().get("distance");
            System.out.println("相似度得分: " + score);
            System.out.println("文档内容: " + doc.getText());
            System.out.println("元数据: " + doc.getMetadata());
            System.out.println("---------------------------");
        }
        // 把召回文档拼接为上下文，交给大模型回答
        String context = retrievedDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        System.out.println("context = " + context);
        
    }
    @Test
    public void testEmbedding() {
        // 1.测试数据
        // 1.1.用来查询的文本
        String query = "早餐吃啥";

        // 1.2.用来做比较的文本
        String[] texts = new String[]{
                "吃包子",
                "日本航空基地水井中检测出有机氟化物超标",
                "国家游泳中心（水立方）：恢复游泳、嬉水乐园等水上项目运营",
                "吃面条",
        };
        // 2.向量化
        // 2.1.先将查询文本向量化
        float[] queryVector = embeddingModel.embed(query);

        // 2.2.再将比较文本向量化，放到一个数组
        List<float[]> textVectors = embeddingModel.embed(Arrays.asList(texts));

        // 3.比较欧氏距离
        // 3.1.把查询文本自己与自己比较，肯定是相似度最高的，值越小越高
        System.out.println(VectorDistanceUtils.euclideanDistance(queryVector, queryVector));
        // 3.2.把查询文本与其它文本比较
        for (float[] textVector : textVectors) {
            System.out.println(VectorDistanceUtils.euclideanDistance(queryVector, textVector));
        }
        System.out.println("------------------");

        // 4.比较余弦距离
        // 4.1.把查询文本自己与自己比较，肯定是相似度最高的，值越小越高
        System.out.println(VectorDistanceUtils.cosineDistance(queryVector, queryVector));
        // 4.2.把查询文本与其它文本比较
        for (float[] textVector : textVectors) {
            System.out.println(VectorDistanceUtils.cosineDistance(queryVector, textVector));
        }
    }

}