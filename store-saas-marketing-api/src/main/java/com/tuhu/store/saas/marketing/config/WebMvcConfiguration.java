package com.tuhu.store.saas.marketing.config;

import com.tuhu.store.saas.marketing.interceptor.CustomerInterceptor;
import com.tuhu.store.saas.marketing.interceptor.EndUserInterceptor;
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

    @Autowired
    private EndUserInterceptor endUserInterceptor;

   // @Autowired
  //  private CustomerInterceptor customerInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        //车主端拦截器
     //   registry.addInterceptor(customerInterceptor).addPathPatterns("/mini/c/**");
        //B端拦截器
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/card/**", "/marketing/**", "/mini/**", "/finance/**", "/distribution/**", "/order/reservation/**", "/customer/group/**")
                .excludePathPatterns("/feign/**", "/**/client/detail","/mini/c/coupon/**","/mini/card/client/query","/mini/card/consumptionHistory");

        registry.addInterceptor(endUserInterceptor)
                .addPathPatterns("/client/**","/mini/c/coupon/**","/mini/card/client/query")
                .excludePathPatterns("/feign/endUser/**","/mini/c/coupon/open/**");
    }

}
