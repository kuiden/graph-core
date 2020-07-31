package com.tuhu.store.saas.marketing.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile({"local", "dev", "test", "tuhutest"})// 设置 dev test 环境开启 prod 环境就关闭了
public class Swagger2 {
    @Bean
    public Docket api() {
        ParameterBuilder tokenParam = new ParameterBuilder();
        List<Parameter> parameters = new ArrayList<Parameter>();
        tokenParam.name(HttpHeaders.AUTHORIZATION).description("access token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build(); //header中的Authorization参数非必填，不需要登录认证时可不填
        parameters.add(tokenParam.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tuhu.store.saas.marketing"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("strore-saas-marketing相关接口").description("strore-saas-marketing 营销相关接口文档")
                .build();
    }


}
