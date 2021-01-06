package com.tuhu.store.saas.marketing.openapi;

import com.alibaba.fastjson.JSON;
import com.tuhu.open.sdk.exception.OpenSdkException;
import com.tuhu.open.sdk.util.SignatureUtils;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.order.request.openApi.OpenApiReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangxiang2
 */
@Slf4j
@Component
public class OpenApiInvoke {
    @Autowired
    private RestTemplate restTemplate;

    public Object sendOpenApiInvoke(OpenApiReq openApiReq, Map<String, Object> bizContent) {
        Object object = null;
        try {
            log.info("start invoke openApi,param:" + JSON.toJSON(bizContent));
            //OpenApi参数
            Map<String, String> invokeContent = new HashMap<>();
            //app_id 开放平台分配给第三方的应用ID,也就是上面创建的应用AppId
            invokeContent.put("app_id", openApiReq.getAppId());
            //调用的已经授权过的服务接口名称
            invokeContent.put("method", openApiReq.getMethod());
            //仅支持JSON
            invokeContent.put("format", "JSON");
            //请求使用的编码格式，仅支持UTF-8，大小写敏感
            invokeContent.put("charset", "UTF-8");
            //生成签名字符串所使用的签名算法类型，目前支持RSA2
            invokeContent.put("sign_type", "RSA2");
            //发送请求的时间戳
            invokeContent.put("timestamp", System.currentTimeMillis() + "");
            //调用的接口版本，固定为：1.0
            invokeContent.put("api_version", StringUtils.isEmpty(openApiReq.getApiVersion()) ? "1.0" : openApiReq.getApiVersion());
            //传递自己的业务参数
            invokeContent.put("biz_content", JSON.toJSONString(bizContent));

            //invokeContent.put("variable_uri", openApiReq.getVariableUri());
            //创建应用中设置的秘钥对中的私钥
            String privateKey = openApiReq.getPrivateKey();
            //获取排序后的内容，签名前需要对参数进行排序
            String content = SignatureUtils.getSignOrSignCheckContent(invokeContent);
            //生成签名
            String sign = SignatureUtils.rsa256Sign(content, privateKey, "UTF-8");
            invokeContent.put("sign", sign);
            //发起调用，测试环境
            object = restTemplate.postForObject(openApiReq.getGatewayUrl(), invokeContent, Object.class);
            log.info("openApi invokeContent入参：" + JSON.toJSONString(invokeContent));
            log.info("openApi入参：" + JSON.toJSONString(openApiReq) + ",业务参数：" + JSON.toJSONString(bizContent) + ",openApi返回结果：" + JSON.toJSONString(object));
        } catch (OpenSdkException e) {
            log.error("调用openApi接口异常", e);
            throw new StoreSaasMarketingException("调用openApi接口异常：" + JSON.toJSONString(openApiReq));
        } finally {
        }
        return object;
    }



    public Object sendGetOpenApiInvoke(OpenApiReq openApiReq, Map<String, Object> bizContent) {
        Object object = null;
        try {
            log.info("start invoke openApi,param:" + JSON.toJSON(bizContent));
            //OpenApi参数
            Map<String, String> invokeContent = new HashMap<>();
            //app_id 开放平台分配给第三方的应用ID,也就是上面创建的应用AppId
            invokeContent.put("app_id", openApiReq.getAppId());
            //调用的已经授权过的服务接口名称
            invokeContent.put("method", openApiReq.getMethod());
            //仅支持JSON
            invokeContent.put("format", "JSON");
            //请求使用的编码格式，仅支持UTF-8，大小写敏感
            invokeContent.put("charset", "UTF-8");
            //生成签名字符串所使用的签名算法类型，目前支持RSA2
            invokeContent.put("sign_type", "RSA2");
            //发送请求的时间戳
            invokeContent.put("timestamp", System.currentTimeMillis() + "");
            //调用的接口版本，固定为：1.0
            invokeContent.put("api_version", StringUtils.isEmpty(openApiReq.getApiVersion()) ? "1.0" : openApiReq.getApiVersion());
            //传递自己的业务参数
            invokeContent.put("biz_content", JSON.toJSONString(bizContent));
            invokeContent.put("variable_uri", openApiReq.getVariableUri());
            //创建应用中设置的秘钥对中的私钥
            String privateKey = openApiReq.getPrivateKey();
            //获取排序后的内容，签名前需要对参数进行排序
            String content = SignatureUtils.getSignOrSignCheckContent(invokeContent);
            //生成签名
            String sign = SignatureUtils.rsa256Sign(content, privateKey, "UTF-8");
            invokeContent.put("sign", sign);
            //发起调用，测试环境
            object = restTemplate.postForObject(openApiReq.getGatewayUrl(), invokeContent, Object.class);
            log.info("openApi invokeContent入参：" + JSON.toJSONString(invokeContent));
            log.info("openApi入参：" + JSON.toJSONString(openApiReq) + ",业务参数：" + JSON.toJSONString(bizContent) + ",openApi返回结果：" + JSON.toJSONString(object));
        } catch (OpenSdkException e) {
            log.error("调用openApi接口异常", e);
            throw new StoreSaasMarketingException("调用openApi接口异常：" + JSON.toJSONString(openApiReq));
        } finally {
        }
        return object;
    }
}
