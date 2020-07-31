package com.tuhu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>
 * 启动类
 * </p>
 *
 * @author luowei1
 * @since 2018-12-12
 */
@EnableSwagger2
@EnableFeignClients(basePackages = { "com.tuhu.store.saas.marketing.remote" })
@EnableHystrix
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy=true)
@Slf4j
public class StartMain {

    public static void main(String[] args) {
        SpringApplication.run(StartMain.class, args);
    }

}
