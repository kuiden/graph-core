package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mengfan.common.util.GatewayClient;
import com.tuhu.store.saas.marketing.dataobject.EndUser;
import com.tuhu.store.saas.marketing.dataobject.OauthClientDetailsDAO;
import com.tuhu.store.saas.marketing.exception.OpenIdException;
import com.tuhu.store.saas.marketing.request.MiniProgramNotifyReq;
import com.tuhu.store.saas.marketing.service.*;
import com.tuhu.store.saas.marketing.util.ImageUtil;
import com.tuhu.store.saas.marketing.util.WxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/6/5
 * Time: 10:54
 * Description:小程序相关功能
 */
@Slf4j
@Service
public class MiniAppServiceImpl implements MiniAppService {
    @Autowired
    ImageUploadService imageUploadService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GatewayClient getwayClient;

    @Autowired
    private ImageUtil imageUtil;

    private String tokenUrl = "https://api.yunquecloud.com/auth/wechat/accessToken";

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.appSecret}")
    private String appSecret;

    @Autowired
    private IOauthClientDetailsService iOauthClientDetailsService;

    @Autowired
    private IWechatService iWechatService;

    @Autowired
    private IEndUserService iEndUserService;

    /**
     * 获取小程序码图片
     *
     * @param scene
     * @param path
     * @param width
     * @return
     */
    @Override
    public String getQrCodeUrl(String scene, String path, Long width) {
        /*
         * 1、获取token
         */
//        String token = getClientAppToken(clientType);
//        if (StringUtils.isBlank(token)) {
//            log.info("getClientAppToken return blank,clientType=" + clientType);
//            return null;
//        }
        String  accessTokenUrl=  "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        accessTokenUrl = String.format(accessTokenUrl, appid, appSecret);
        URI url = UriComponentsBuilder.fromUriString(accessTokenUrl).build().toUri();
        String  wechatRespon = restTemplate.getForEntity(url, String.class).getBody();
        JSONObject  wechatResponJson = JSONObject.parseObject(wechatRespon);
        String token= wechatResponJson.getString("access_token");
        log.info("c端小程序token:{}",token);
        /*
         * 2、调微信api,根据当前各参数生成二维码图片buffer,并转base64编码
         */
        byte[] result = WxUtil.getQrCode(token, scene, path, width);
        /*
         * 3、上传图片到图片服务器
         */
        String fileName = UUID.randomUUID()  + ".jpeg";
        String image = imageUtil.uploadFileToWx(result, fileName, "/store/marketing");

        //String image = imageUploadService.uploadImageByBase64(qrBase64, width, width);
        //上传到腾讯云服务器

        log.info("图片url:{}",image);

        return image;
    }

    /**
     * 获取c端小程序accessToken
     * 调dubbo接口获取AccessToken
     *
     * @return
     */
    private String getClientAppToken(String clientType) {
        String accessToken = "";
        // 调dubbo接口获取AccessToken
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("clientType", clientType);
            String json = getwayClient.getForObject(tokenUrl, map, this.getUUIDHeader(), String.class);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
            if (jsonObject != null && jsonObject.get("data") != null) {
                accessToken = (String) jsonObject.get("data");
            }
        } catch (Exception e) {
            log.error("getClientAppToken error:" + e);
        }

        return accessToken;
    }

    private HttpHeaders getUUIDHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("RequestID", UUID.randomUUID().toString());
        return httpHeaders;
    }

    @Override
    public Object miniProgramNotify(MiniProgramNotifyReq miniProgramNotifyReq) {
        log.info("发送小程序模板消息，request={}", JSONObject.toJSONString(miniProgramNotifyReq));
        if (StringUtils.isEmpty(miniProgramNotifyReq.getOpenId()) && StringUtils.isEmpty(miniProgramNotifyReq.getOpenIdCode()) && StringUtils.isEmpty(miniProgramNotifyReq.getCustomerId())) {
            throw new OpenIdException("通知接收者不能为空");
        }
        String openId = miniProgramNotifyReq.getOpenId();
        if (StringUtils.isEmpty(openId) && StringUtils.isEmpty(miniProgramNotifyReq.getCustomerId())) {
            String clientType = miniProgramNotifyReq.getClientType();
            OauthClientDetailsDAO oauthClientDetails = iOauthClientDetailsService.getClientDetailByClientId(clientType);
            openId = iWechatService.getOpenId(oauthClientDetails.getWxAppid()
                    , oauthClientDetails.getWxSecret()
                    , miniProgramNotifyReq.getOpenIdCode(),
                    oauthClientDetails.getWxOpenidUrl());
        } else if (!StringUtils.isEmpty(miniProgramNotifyReq.getCustomerId())) {
            List<EndUser> endUserList = iEndUserService.findByCustomerId(miniProgramNotifyReq.getCustomerId());
            if (CollectionUtils.isEmpty(endUserList)) {
                throw new OpenIdException("通知接收者未绑定小程序");
            }
            openId = endUserList.get(0).getOpenId();
        }
        miniProgramNotifyReq.setOpenId(openId);
        Object result = iWechatService.miniProgramNotify(openId, miniProgramNotifyReq);
        return result;
    }

}