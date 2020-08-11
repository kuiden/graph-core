package com.tuhu.store.saas.marketing.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/7
 * Time: 9:44
 * Description:
 */
public class WxUtil {
    public static void main(String[] args) {

        String str = "{\"Result\":\"/store/FnhLSey25wIaFfwxoCnliUw6Yl-H_w250_h250.jpeg\",\"ElapsedMilliseconds\":194,\"Success\":true}";
        JSONObject o = (JSONObject) JSONObject.parse(str);
        System.out.println(o.get("Result"));
//        String token="21_F80ekdf39EZSXnv_lOCgKDb5qHboyzx6jW2EKGK23yXqAvQfhLN0kkE4x1xI1iZ61x1aQMunZ5bmNascbqEXsX2FbtaVAJZhlz0mKNcPwUh79R3M5EfdB7J9TWG6ngQIv0KLtvAt8hSpWz2gZATaAHAJKD";
//
//        String baseStr=getQrCode(token,"222");
//
//        System.out.println(baseStr);
//        Map map1= Maps.newHashMap();
//        map1.put("Contents",baseStr);
//        map1.put("MaxWidth",250);
//        map1.put("MaxHeight",250);
//        map1.put("DirectoryName","store");
//        String urlString= HttpClientUtil.doPost("http://wcf.tuhu.work:9010/Utility/FileUpload/UploadImage", JSON.toJSONString(map1), UUID.randomUUID().toString());
//        System.out.println(urlString);
    }

    public static String getQrCode(String token, String scene, String path, Long width) {
        String qr = "";

        try {
            Map map = new HashMap();
            map.put("scene", scene);
            //todo 小程序正式发版后传path
//            if (StringUtils.isEmpty(path)) {
//                map.put("page", "pages/index/index");
//            }else {
//                map.put("page", path);
//            }
            map.put("width", width);
            String data = JSONObject.toJSONString(map);// 转化成json
            String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token;
            qr = httpPostWithJSON(url, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qr;
    }

    private static String httpPostWithJSON(String url, String json) throws IOException {
        String result = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");

        StringEntity se = new StringEntity(json);
        se.setContentType("application/json");
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "UTF-8"));
        httpPost.setEntity(se);
        HttpResponse response = httpClient.execute(httpPost);
        if (response != null) {
            HttpEntity resEntity = response.getEntity();
            InputStream instreams = resEntity.getContent();

            result = getBase64FromInputStream(instreams);
//            result = "data:image/png;base64," + result;
        }
        httpPost.abort();
        return result;
    }

    public static String getBase64FromInputStream(InputStream in) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(Base64.encodeBase64(data));
    }


}