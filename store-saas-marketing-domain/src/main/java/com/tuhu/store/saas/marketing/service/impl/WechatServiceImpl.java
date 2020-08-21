package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mengfan.common.util.GatewayClient;
import com.tuhu.store.saas.marketing.constant.AuthConstant;
import com.tuhu.store.saas.marketing.constant.MiniNotifyConstant;
import com.tuhu.store.saas.marketing.dataobject.OauthClientDetailsDAO;
import com.tuhu.store.saas.marketing.exception.OpenIdException;
import com.tuhu.store.saas.marketing.exception.SaasAuthException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SrvReservationOrderMapper;
import com.tuhu.store.saas.marketing.po.SrvReservationOrder;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.MiniProgramNotifyReq;
import com.tuhu.store.saas.marketing.service.IOauthClientDetailsService;
import com.tuhu.store.saas.marketing.service.IWechatService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.order.dto.serviceorder.ResultDTO;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class WechatServiceImpl implements IWechatService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GatewayClient gatewayClient;

    @Autowired
    private SrvReservationOrderMapper reservationOrderMapper;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private IOauthClientDetailsService iOauthClientDetailsService;

    @Value("${wechat.miniprogram.message.template.send.url:https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=}")
    private String templateMessageSendUrl;

    @Value("${wechat.miniprogram.message.template.id}")
    private String defaultTemplateId;

    @Value("${wechat.access.token.address}")
    private String accessTokenUrl;

    private String tokenUrl = "https://api.yunquecloud.com/auth/wechat/accessToken";

    @Override
    public String getOpenIdFromTuhu(String appid, String code, String merchantNo, String openIdUrl) {
        URI url = UriComponentsBuilder.fromUriString(openIdUrl).build().toUri();
        JSONObject requestJson = new JSONObject();
        requestJson.put("appId", appid);
        requestJson.put("merchantNo", code);
        requestJson.put("js_code", merchantNo);
        log.info("获取用户的openid，参数如下：{} url:{}", requestJson.toJSONString(), url);
        JSONObject resultJson = restTemplate.postForObject(url, requestJson, JSONObject.class);
        log.info("获取微信session结果:{}", resultJson.toJSONString());
        return requestJson.getString("openId");
    }

    @Override
    public String getOpenId(String appid, String appSecret, String code, String openIdUrl) {
        JSONObject wechatResponJson = null;
        try {
            log.info("调用openID接口参数：appid:{} appSecret:{} code:{} openIdUrl:{}", appid, appSecret, code, openIdUrl);
            String key = AuthConstant.loginWechatUserHeader + code;
            String wechatRespon = stringRedisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(wechatRespon)) {
                openIdUrl = openIdUrl + "&appid=" + appid + "&secret=" + appSecret + "&js_code=" + code;
                URI url = UriComponentsBuilder.fromUriString(openIdUrl).build().toUri();
                wechatRespon = restTemplate.getForEntity(url, String.class).getBody();
            }
            log.info("调用openID接口返回值：{}", wechatRespon);
            if (StringUtils.isEmpty(wechatRespon)) {
                throw new OpenIdException("调用openID接口失败 code :" + code);
            }
            wechatResponJson = JSONObject.parseObject(wechatRespon);
            if (!wechatResponJson.containsKey("openid")) {
                throw new OpenIdException("调用openID接口失败 code :" + code);
            }
            //缓存微信用户的openId及session_key
            stringRedisTemplate.opsForValue().set(key, wechatRespon, 4, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("调用openID接口失败 参数：appid:{} appSecret:{} code:{} openIdUrl:{},error:{}", appid, appSecret, code, openIdUrl, ExceptionUtils.getStackTrace(e));
            throw new OpenIdException("调用openID接口失败 code :" + code);
        }
        return wechatResponJson.getString("openid");
    }

    @Override
    public String refreshAccessToken(String appid, String appSecret, String clinetType, String accessTokenUrl) {
        String wechatRespon = null;
        JSONObject wechatResponJson = null;
        try {
            log.info("调用Access Token接口参数：appid:{} appSecret:{} clientType:{} accessTokenUrl:{}", appid, appSecret, clinetType, accessTokenUrl);
            String key = AuthConstant.wechatAccessTokenHeader + clinetType;
            wechatRespon = stringRedisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(wechatRespon)) {
                accessTokenUrl = String.format(accessTokenUrl, appid, appSecret);
                URI url = UriComponentsBuilder.fromUriString(accessTokenUrl).build().toUri();
                wechatRespon = restTemplate.getForEntity(url, String.class).getBody();
            }
            log.info("调用Access Token接口返回值：{}", wechatRespon);
            if (StringUtils.isEmpty(wechatRespon)) {
                throw new SaasAuthException("调用Access Token接口失败 clinetType :" + clinetType);
            }
            wechatResponJson = JSONObject.parseObject(wechatRespon);
            if (!wechatResponJson.containsKey("access_token")) {
                throw new SaasAuthException("调用Access Token接口失败 clinetType :" + clinetType);
            }
            //缓存微信的access_token
            Integer expiresIn = wechatResponJson.getInteger("expires_in");
            if (null == expiresIn || expiresIn <= 0) {
                expiresIn = AuthConstant.wechatAccessTokenExpiresIn;
            }
            expiresIn = expiresIn * 5 / 6;//提前过期
            stringRedisTemplate.opsForValue().set(key, wechatRespon, expiresIn, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("调用Access Token接口失败 参数：appid:{} appSecret:{} clinetType:{} accessTokenUrl:{}", appid, appSecret, clinetType, accessTokenUrl);
            throw new SaasAuthException("调用Access Token接口失败 clinetType :" + clinetType);
        }
        return wechatRespon;
    }

    @Override
    public String forceRefreshAccessToken(String appid, String appSecret, String clinetType, String accessTokenUrl) {
        String wechatRespon = null;
        JSONObject wechatResponJson = null;
        try {
            log.info("调用强制刷新Access Token接口参数：appid:{} appSecret:{} clientType:{} accessTokenUrl:{}", appid, appSecret, clinetType, accessTokenUrl);
            String key = AuthConstant.wechatAccessTokenHeader + clinetType;
            accessTokenUrl = String.format(accessTokenUrl, appid, appSecret);
            URI url = UriComponentsBuilder.fromUriString(accessTokenUrl).build().toUri();
            wechatRespon = restTemplate.getForEntity(url, String.class).getBody();
            log.info("调用Access Token接口返回值：{}", wechatRespon);
            if (StringUtils.isEmpty(wechatRespon)) {
                throw new SaasAuthException("调用Access Token接口失败 clinetType :" + clinetType);
            }
            wechatResponJson = JSONObject.parseObject(wechatRespon);
            if (!wechatResponJson.containsKey("access_token")) {
                throw new SaasAuthException("调用Access Token接口失败 clinetType :" + clinetType);
            }
            //缓存微信的access_token
            Integer expiresIn = wechatResponJson.getInteger("expires_in");
            if (null == expiresIn || expiresIn <= 0) {
                expiresIn = AuthConstant.wechatAccessTokenExpiresIn;
            }
            expiresIn = expiresIn * 5 / 6;//提前过期
            stringRedisTemplate.opsForValue().set(key, wechatRespon, expiresIn, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("调用强制刷新Access Token接口失败 参数：appid:{} appSecret:{} clinetType:{} accessTokenUrl:{}", appid, appSecret, clinetType, accessTokenUrl);
            throw new SaasAuthException("调用强制刷新Access Token接口失败 clinetType :" + clinetType);
        }
        return wechatRespon;
    }

    @Override
    public Object miniProgramNotify(String openId, MiniProgramNotifyReq miniProgramNotifyReq) {
        ResultDTO<String> accessTokenData = this.getWechatAccessTokenByClientTypeNoCache(miniProgramNotifyReq.getClientType());
        String sendUrl = templateMessageSendUrl.concat(accessTokenData.getData());
        Map<String, Object> param = new HashMap();
        param.put("touser", openId);
        String templateId = miniProgramNotifyReq.getTemplateId();
        if (null == templateId) {
            templateId = defaultTemplateId;
        }
        param.put("template_id", templateId);
        param.put("page", miniProgramNotifyReq.getPage());
        //param.put("form_id", miniProgramNotifyReq.getFormId());
        HashMap data = Maps.newHashMap();
        SrvReservationOrder srvReservationOrder=reservationOrderMapper.selectById(miniProgramNotifyReq.getFormId());
        if (Objects.nonNull(srvReservationOrder)) {
            StoreInfoVO storeInfoVO = new StoreInfoVO();
            storeInfoVO.setStoreId(srvReservationOrder.getStoreId());
            storeInfoVO.setTanentId(srvReservationOrder.getTenantId());
            try {
                StoreDTO storeInfoDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();
                if (Objects.nonNull(storeInfoDTO)) {
                    //门店名
                    HashMap thing10Value = Maps.newHashMap();
                    String storeName= org.apache.commons.lang3.StringUtils.isNotBlank(storeInfoDTO.getStoreName())?storeInfoDTO.getStoreName().substring(0,20): MiniNotifyConstant.STORENAME;
                    thing10Value.put("value",storeName);
                    data.put("thing10",thing10Value);

                    //时间
                    HashMap date5 = Maps.newHashMap();
                    if(Objects.nonNull(srvReservationOrder.getEstimatedArriveTime())){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        date5.put("value", dateFormat.format(srvReservationOrder.getEstimatedArriveTime()));
                    }else{
                        date5.put("value", MiniNotifyConstant.ESTIMATEDARRIVETIME);
                    }
                    data.put("date5",date5);

                    //地址
                    HashMap thing16 = Maps.newHashMap();
                    String address= org.apache.commons.lang3.StringUtils.isNotBlank(storeInfoDTO.getAddress())?storeInfoDTO.getAddress(): MiniNotifyConstant.ADDRESS;
                    thing16.put("value",address);
                    data.put("thing16",thing16);


                    //电话
                    HashMap phone_number12 = Maps.newHashMap();
                    String phoneNumber= org.apache.commons.lang3.StringUtils.isNotBlank(srvReservationOrder.getCustomerPhoneNumber())?srvReservationOrder.getCustomerPhoneNumber(): MiniNotifyConstant.PHONENUMBER;
                    phone_number12.put("value",phoneNumber);
                    data.put("phone_number12",phone_number12);

                    //备注
                    HashMap thing15 = Maps.newHashMap();
                    String description= org.apache.commons.lang3.StringUtils.isNotBlank(srvReservationOrder.getDescription())?srvReservationOrder.getDescription(): MiniNotifyConstant.DESCRIPTION;
                    thing15.put("value",description);
                    data.put("thing15",thing15);
                }
            } catch (Exception e) {
                log.error("查询门店信息RPC接口异常", e);
                throw new SaasAuthException("查询门店信息RPC接口异常:" + miniProgramNotifyReq.getFormId());
            }
        }else{
            throw new SaasAuthException("没有找到预约单详情:" + miniProgramNotifyReq.getFormId());
        }
        param.put("data", data);
        param.put("emphasis_keyword", miniProgramNotifyReq.getEmphasisKeyword());
        try {
            log.info("发送小程序模板消息通知，request={}", JSONObject.toJSONString(param));
            String result = restTemplate.postForObject(sendUrl, param, String.class);
            log.info("发送小程序模板消息通知，response={}", result);
            JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
            return jsonObject;
        } catch (Exception e) {
            log.error("miniProgramNotify error:", e);
        }
        return null;
    }




    private ResultDTO<String> getWechatAccessTokenByClientTypeNoCache(String clientType) {
        ResultDTO<String> resultDTO = new ResultDTO<>();
        if (StringUtils.isEmpty(clientType)) {
            resultDTO.setCode(AuthConstant.validCode);
            resultDTO.setMsg("clientType不能为空");
            return resultDTO;
        }
        OauthClientDetailsDAO clientDetails = iOauthClientDetailsService.getClientDetailByClientId(clientType);
        if (null == clientDetails) {
            resultDTO.setCode(AuthConstant.validCode);
            resultDTO.setMsg("clientType不存在");
            return resultDTO;
        }
        String cachedToken = null;
        try {
            cachedToken = this.forceRefreshAccessToken(clientDetails.getWxAppid(), clientDetails.getWxSecret(), clientType, accessTokenUrl);
        } catch (Exception e) {
            log.error("刷新微信Access Token异常，", e);
        }
        if (StringUtils.isEmpty(cachedToken)) {
            resultDTO.setCode(AuthConstant.exceptionCode);
            resultDTO.setMsg("刷新获取微信Access Token失败");
        } else {
            JSONObject tokenJSON = JSONObject.parseObject(cachedToken);
            resultDTO.setCode(AuthConstant.succesCode);
            resultDTO.setData(tokenJSON.getString("access_token"));
        }
        return resultDTO;
    }

    /**
     * 获取小程序的AccessToken
     *
     * @param clientType
     * @return
     */
    private String getClientAppToken(String clientType) {
        String accessToken = "";
        // 调dubbo接口获取AccessToken
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("clientType", clientType);
            String json = gatewayClient.getForObject(tokenUrl, map, this.getUUIDHeader(), String.class);
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
}
