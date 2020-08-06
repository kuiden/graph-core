package com.tuhu.store.saas.marketing.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
//        String xid = RootContext.getXID();
//        if (!org.apache.commons.lang3.StringUtils.isEmpty(xid)) {
//            //构建请求头
//            requestTemplate.header("TX_XID", xid);
//        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == attributes) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        //添加token
        if (StringUtils.isNotBlank(request.getHeader(HttpHeaders.AUTHORIZATION))) {
            requestTemplate.header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
        }
        // requestId
        String requsetId = MDC.get("requestId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(requsetId)) {
            requestTemplate.header("requestId", requsetId);
        }
    }
}
