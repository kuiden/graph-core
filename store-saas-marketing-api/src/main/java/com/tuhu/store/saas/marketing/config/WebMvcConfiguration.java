package com.tuhu.store.saas.marketing.config;

import com.tuhu.store.saas.marketing.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * mvc配置
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private UserInterceptor userInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor).addPathPatterns("/marketing/**", "/mini/**", "/finance/**","/distribution/**").excludePathPatterns("/feign/**");
    }

}
