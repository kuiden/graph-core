package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mengfan.common.util.GatewayClient;
import com.tuhu.store.saas.marketing.service.ImageUploadService;
import com.tuhu.store.saas.marketing.util.WxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/31
 * Time: 11:14
 * Description:
 */
@Service
@Slf4j
public class ImageUploadServiceImpl implements ImageUploadService {
    @Autowired
    private GatewayClient getwayClient;

    @Value("${tuhu.img.upload}")
    private String uploadImgUrl;

    private final String PIC_URL_DOMAIN = "http://img3.tuhu.org";

    @Override
    public String uploadImageByInputStream(InputStream inputStream, Long width, Long height) {
        String base64= WxUtil.getBase64FromInputStream(inputStream);
        return this.uploadImageByBase64(base64, width, height);
    }

    /**
     * 上传图片到图片服务器
     * @param base64
     * @param width 图片宽
     * @param height 图片高
     * @return
     */
    @Override
    public String uploadImageByBase64(String base64, Long width, Long height) {
        if (org.apache.commons.lang3.StringUtils.isBlank(base64)) {
            return null;
        }

        String urlString = "";
        try {
            Map map = Maps.newHashMap();
            map.put("Contents", base64);
            map.put("MaxWidth", width);
            map.put("MaxHeight", height);
            map.put("DirectoryName", "store");
            String json = getwayClient.postForObject(uploadImgUrl, map, this.getUUIDHeader(), String.class);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
            log.info("上传图片返回参数：{}",JSONObject.toJSON(jsonObject));
            if (jsonObject != null && jsonObject.get("Result") != null) {
                urlString = PIC_URL_DOMAIN + jsonObject.get("Result");
            }
        } catch (Exception e) {
            log.error("upload image error:" + e);
        }

        return urlString;
    }

    private HttpHeaders getUUIDHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("RequestID", UUID.randomUUID().toString());
        return httpHeaders;
    }
}