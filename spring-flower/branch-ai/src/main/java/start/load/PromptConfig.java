package start.load;


import cn.hutool.core.io.IoUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
public class PromptConfig {

    @Value("${system.chat.file:classpath:system-message.txt}")
    private String systemChatFilePath;

    private final AtomicReference<String> chatSystemMessage = new AtomicReference<>();

    @PostConstruct
    public void init() {
        loadSystemPrompt();
    }

    private void loadSystemPrompt() {
        ClassPathResource resource = new ClassPathResource(systemChatFilePath.replace("classpath:", ""));
        try(InputStream is = resource.getInputStream()){
            String content = IoUtil.readUtf8(is);
            chatSystemMessage.set(content);
            log.info("成功加载系统提示词文件: {}, 内容长度: {} 字符", systemChatFilePath, content.length());
        } catch (IOException e) {
            log.error("加载系统提示词文件失败: {}", systemChatFilePath, e);
            chatSystemMessage.set("");
        }
    }
}
