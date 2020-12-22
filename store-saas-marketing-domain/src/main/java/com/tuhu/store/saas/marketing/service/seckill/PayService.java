package com.tuhu.store.saas.marketing.service.seckill;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Maps;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.finance.auth.common.dto.ExtParameter;
import com.tuhu.finance.auth.common.dto.GetAuthTokenReq;
import com.tuhu.springcloud.common.util.Money;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import com.tuhu.store.saas.marketing.openapi.OpenApiInvoke;
import com.tuhu.store.saas.marketing.remote.request.CashierRequestVO;
import com.tuhu.store.saas.marketing.util.AuthSignUtil;
import com.tuhu.store.saas.order.request.openApi.OpenApiReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author wangxiang2
 */
@Component
@Slf4j
public class PayService {


    @Autowired
    private OpenApiInvoke openApiInvoke;

    @Value("${pay.client_id}")
    private String clientId;

    @Value("${pay.product_category}")
    private String productCategory;

    @Value("${pay.payment_type}")
    private String paymentType;

    @Value("${pay.requested_platform_code}")
    private String requestedPlatformCode;

    @Value("${pay.sign_key}")
    private String signKey;

    @Value("${pay.callback_url}")
    private String callbackUrl;

    @Value("${pay.source_system_code}")
    private String sourceSystemCode;

    @Value("${pay.product_desc}")
    private String productDesc;

    @Value("${pay.user_source}")
    private String userSource;

    @Value("${open.api.gateway.url}")
    private String tuhuOpenApiGateWayUrl;
    @Value("${open.api.tuhu.private.key}")
    private String tuhuOpenApiPrivateKey;




    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;


/*    public Object queryPaymentResult(String id) {
        Object result = new Object();
        OpenApiReq openApiReq = this.getPaymentResultOpenApi();
        Map<String, Object> mapRequest = Maps.newHashMap();
        String variableUri = "/" + productCategory + "/" + paymentType + "/" + id + "/";
        openApiReq.setVariableUri(variableUri);
        //查询支付结果
        log.info("queryPaymentResult, request:" + JSON.toJSONString(mapRequest));
        result = openApiInvoke.sendGetOpenApiInvoke(openApiReq, mapRequest);
        //解析参数获取token
        log.info("queryPaymentResult, response:" + JSON.toJSONString(result));
        return result;
    }*/

    public Map<String, Object> getPayAuthToken(SeckillRegistrationRecord seckillRegistrationRecord, String tradeOrderId) {
        Map<String, Object> returnMap = Maps.newHashMap();
        try {
            OpenApiReq openApiReq = this.getAuthTokenOpenApi();
            String tokenRequest = this.getRequestParameter(seckillRegistrationRecord, tradeOrderId);

            Map<String, Object> tokenRequestMap = JSONObject.parseObject(tokenRequest);
            log.info("getPayAuthToken, request:" + tokenRequest);
            //调用获取收银台token
            Object tokenResult = openApiInvoke.sendOpenApiInvoke(openApiReq, tokenRequestMap);
            //解析参数获取token
            log.info("getPayAuthToken, response:" + JSON.toJSONString(tokenResult));
            JSONObject out = JSONObject.parseObject(JSON.toJSONString(tokenResult));
            String inString = out.getString("data");
            JSONObject indata = JSONObject.parseObject(inString);
            //String returnCode = indata.getString("returnCode");
            String dataStr = indata.getString("data");
            JSONObject innerdata = JSONObject.parseObject(dataStr);
            returnMap = (Map<String, Object>) innerdata;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("lock error", e);
        }
        return returnMap;
    }


/*    private OpenApiReq getPaymentResultOpenApi() {
        OpenApiReq openApiReq = new OpenApiReq();
        openApiReq.setGatewayUrl(tuhuOpenApiGateWayUrl);
        openApiReq.setAppId("store-saas-order");
        openApiReq.setPrivateKey(tuhuOpenApiPrivateKey);
        //openApiReq.setMethod("int-website-fin-payment-engine-query.payQuery.queryPaymentResultByBizOrderNo.get");
        openApiReq.setMethod(queryPaymentResultByBizOrderNoUrl);
        return openApiReq;
    }*/

    private OpenApiReq getAuthTokenOpenApi() {
        OpenApiReq openApiReq = new OpenApiReq();
        openApiReq.setGatewayUrl(tuhuOpenApiGateWayUrl);
        openApiReq.setAppId("store-saas-order");
        openApiReq.setPrivateKey(tuhuOpenApiPrivateKey);
        openApiReq.setMethod("finance-auth-server.auth.getAuthToken.post");
        return openApiReq;
    }


    private String getRequestParameter(SeckillRegistrationRecord seckillRegistrationRecord, String tradeOrderId) {
        //String outBizNo = seckillRegistrationRecord.getId();
        Long amount = seckillRegistrationRecord.getExpectAmount().multiply(new BigDecimal(100)).longValue();
        long payTime = System.currentTimeMillis();
        GetAuthTokenReq getAuthTokenReq = new GetAuthTokenReq();
        getAuthTokenReq.setServerCode("CASHIER");
        getAuthTokenReq.setClientId(clientId);
        getAuthTokenReq.setOpenId("1");
        getAuthTokenReq.setTimeStamp(String.valueOf(payTime));
        List<ExtParameter> list = new ArrayList<>();
        ExtParameter extParameter = new ExtParameter();
        //固定值
        extParameter.setKey("cashierRequest");
        CashierRequestVO cashierRequestVO = new CashierRequestVO();
        cashierRequestVO.setProductCategory(productCategory);
        cashierRequestVO.setPaymentType(paymentType);
        cashierRequestVO.setOutBizNo(tradeOrderId);
        //cashierRequestVO.setTerminalType(onlineReceiveReq.getTerminalType());
        cashierRequestVO.setSourceSystemCode(sourceSystemCode);
        cashierRequestVO.setRequestedPlatformCode(requestedPlatformCode);
        cashierRequestVO.setOrderDesc(productDesc);
        cashierRequestVO.setPayAmount(new Money(amount));
        cashierRequestVO.setOrderAmount(new Money(amount));
        cashierRequestVO.setProductName(productDesc);
        cashierRequestVO.setPayTime(payTime);
        cashierRequestVO.setUserSource(userSource);
        cashierRequestVO.setNotifyUrl(callbackUrl);
        //cashierRequestVO.setReturnUrl(onlineReceiveReq.getReturnUrl() + "/" + outBizNo);
        cashierRequestVO.setVirtual("false");
        cashierRequestVO.setSupportedPay("WX_BARCODE");
        //设置途虎企业id
//        StoreInfoVO storeInfoVO = new StoreInfoVO();
//        storeInfoVO.setStoreId(seckillRegistrationRecord.getStoreId());
//        storeInfoVO.setTanentId(seckillRegistrationRecord.getTenantId());
//        StoreBankInfoDTO storeBankInfoDTO = this.getStoreBankInfo(storeInfoVO);
//        cashierRequestVO.setCompanyId(String.valueOf(storeBankInfoDTO.getTuhuTenantId()));
//        if (PaymentModeCodeEnum.WECHAT.status.equals(tradeOrder.getPaymentModeCode().status)) {
//            cashierRequestVO.setSupportedPay("WX_BARCODE");
//        } else if (PaymentModeCodeEnum.ALIPAY.status.equals(tradeOrder.getPaymentModeCode().status)) {
//            cashierRequestVO.setSupportedPay("ALIPAY_BARCODE");
//        }
        String value = JSON.toJSONString(cashierRequestVO);
        extParameter.setValue(value);
        list.add(extParameter);
        getAuthTokenReq.setExtParameters(list);
        //生成签名
        SortedMap<String, String> reqMap = JSON.parseObject(JSON.toJSONString(getAuthTokenReq), new TypeReference<SortedMap<String, String>>() {
        }, Feature.OrderedField);
        String sign = "";
        try {
            String s = AuthSignUtil.buildSignStr(reqMap);
            sign = AuthSignUtil.createSign(signKey, s);
        } catch (UnsupportedEncodingException e) {
            log.error("支付生成签名异常", e);
            throw new BizException(BizErrorCodeEnum.PROCESS_FAIL, "支付生成签名异常");
        } catch (NoSuchAlgorithmException e) {
            log.error("支付生成签名异常", e);
            throw new BizException(BizErrorCodeEnum.PROCESS_FAIL, "支付生成签名异常");
        } catch (InvalidKeyException e) {
            log.error("支付生成签名异常", e);
            throw new BizException(BizErrorCodeEnum.PROCESS_FAIL, "支付生成签名异常");
        }
        getAuthTokenReq.setSign(sign);
        String json = JSON.toJSONString(getAuthTokenReq);
        return json;
    }

}
