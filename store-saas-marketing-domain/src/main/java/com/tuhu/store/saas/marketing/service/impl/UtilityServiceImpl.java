package com.tuhu.store.saas.marketing.service.impl;

import com.mengfan.common.util.GatewayClient;
import com.tuhu.store.saas.marketing.service.IUtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class UtilityServiceImpl implements IUtilityService {

    @Value("${utility.server.baseurl}")
    private String alrAccessoryProductUrl;

    @Autowired
    private GatewayClient gatewayClient;

    /**
     * 获取短链接口
     * @param longUrl
     * @return
     */
    @Override
    public String getShortUrl(String longUrl) {
        try {
            log.info("获取短链接口参数如下：{}", longUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.add("RequestID", UUID.randomUUID().toString());
            Map<String,Object> params = new HashMap<>();
            params.put("uri",longUrl);
            params.put("source","store_saas");
            Map<String,Object> result = gatewayClient.getForObject(alrAccessoryProductUrl+"/Utility/Utility/GetTuhuDwz",params,headers,Map.class);
            log.info("获取短链返回值：{}", result);
            if(result==null||!(Boolean)result.get("Success")){
                return null;
            }
            return (String) result.get("Result");
        } catch (Exception e) {
            log.error("获取短链接口异常：{}", e);
            return null;
        }
    }
}
