package com.tuhu.store.saas.marketing.util;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;


public class AuthSignUtil {
    private static final Logger logger = LoggerFactory.getLogger(com.tuhu.store.saas.marketing.util.AuthSignUtil.class);
    public static String ALG = "HmacSHA256";

    /**
     * 生成签名字符串
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String buildSignStr(Map<String, String> params) throws UnsupportedEncodingException {
        return buildSignStr(params, false);
    }

    /**
     * 生成签名字符串
     *
     * @param params
     * @param encode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String buildSignStr(Map<String, String> params, boolean encode) throws UnsupportedEncodingException {
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = String.valueOf(value);
            }
            if (encode) {
                temp.append(URLEncoder.encode(valueString, "UTF-8"));
            } else {
                temp.append(valueString);
            }
        }

        return temp.toString();
    }

    /**
     * 生成签名
     *
     * @param key
     * @param content
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    public static String createSign(String key, String content)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        return createSign(key, ALG, content);
    }

    /**
     * 生成签名
     *
     * @param key
     * @param alg
     * @param content
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String createSign(String key, String alg, String content)
            throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {

        Mac mac = Mac.getInstance(alg);
        //get the bytes of the hmac key and data string
        byte[] secretByte = key.getBytes("UTF-8");
        byte[] dataBytes = content.getBytes("UTF-8");
        SecretKey secret = new SecretKeySpec(secretByte, alg);

        logger.debug("加密key:{}加密算法:{}加密明文:{}", key, alg, content);
        logger.info("加密算法:{}加密明文:{}", alg, content);
        mac.init(secret);
        byte[] doFinal = mac.doFinal(dataBytes);
        byte[] hexB = new Hex().encode(doFinal);
        String checksum = new String(hexB);
        logger.info("checksum:{}", checksum);
        return checksum;
    }

    /**
     * @param exptSign
     * @param actualSign
     * @return
     */
    public static boolean checkSign(String exptSign, String actualSign) {
        if (actualSign.equals(exptSign)) {
            return true;
        } else {
            logger.warn("Auth验签-异常：\n服务生成签名:{}\n实际请求签名:{}", exptSign, actualSign);
            return false;
        }
    }

}
