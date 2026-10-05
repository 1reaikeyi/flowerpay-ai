package com.branch.config;

import framework.properties.AliOssProperties;
import framework.util.AliOssUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {
    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        return new AliOssUtil(aliOssProperties);
    }
}
