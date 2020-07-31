package com.tuhu.store.saas.marketing.config;

/**
 * 配置跨域访问
 * Created by wangxiangyun on 2018/7/24.
 */
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    @Value("${saas.cors.config.origin.urls:*}")
    private String saasOriginsUrl;
    
    private CorsConfiguration buildConfig() {
        List<String> urls = Arrays.asList(  saasOriginsUrl.split(","));
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*"); // 2
        corsConfiguration.addAllowedMethod("*"); // 3
        corsConfiguration.setAllowedOrigins(urls);
        return corsConfiguration;
    }
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4
        return new CorsFilter(source);
    }
    
    
}