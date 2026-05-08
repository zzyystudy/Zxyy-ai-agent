package com.Zxyy.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置跨域策略
 * 在生产级项目中可能还要更改
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")      // 所有接口
                .allowedOriginPatterns("*")    // 允许所有来源（生产建议写具体域名）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true) // 是否允许Cookie
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}