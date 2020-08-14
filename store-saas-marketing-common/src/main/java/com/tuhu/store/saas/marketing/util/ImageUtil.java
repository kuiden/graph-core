package com.tuhu.store.saas.marketing.util;

import com.tuhu.huapei.service.cos.CosService;
import com.tuhu.store.saas.marketing.enums.UploadImgEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.remote.UploadImgRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author wangyuqing
 * @since 2020/8/12 15:53
 */
@Service
@Slf4j
public class ImageUtil {

    @Autowired
    private CosService cosService;

    public static final String[] IMG_SUFFIX= {"png","jpg","jpeg","gif","tif","pcx","bmp","tga","svg","psd"};

    public UploadImgRes uploadFile(MultipartFile imgFile, String indexNameParams) {
        try {
            String indexName = changeIndexName(indexNameParams);
            UploadImgRes res = new UploadImgRes();
            //先上传原件
            InputStream inputStream = imgFile.getInputStream();
            byte[] baseBytes = PicUtil.readInputStream(inputStream);
            String baseImgName = UUID.randomUUID().toString() + "_" + imgFile.getOriginalFilename();
            String baseImgPath = uploadFileToWx(baseBytes, baseImgName, indexName);
            res.setImgUrl(baseImgPath);

            try {
                String suffix = imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().lastIndexOf(".") + 1);
                //如果不是图片文件，或者是gif都不需要压缩
                if(!Arrays.asList(IMG_SUFFIX).contains(suffix.toLowerCase()) || "gif".equals(suffix)){
                    res.setThumImgUrl(URLDecoder.decode(baseImgPath, "UTF-8"));
                    return res;
                }
                //上传压缩图片
                byte[] thumBytes = PicUtil.compressPicForScale(baseBytes);
                String thumImgName = UUID.randomUUID().toString() + "_thum_" + imgFile.getOriginalFilename();
                String thumImgPath = uploadFileToWx(thumBytes, thumImgName, indexName);
                res.setThumImgUrl(URLDecoder.decode(thumImgPath, "UTF-8"));
            } catch (Exception ex) {
                log.error("压缩图片发送异常", ex.getMessage());
                res.setThumImgUrl(URLDecoder.decode(baseImgPath, "UTF-8"));
            }
            return res;
        } catch (Exception ex) {
            log.error("上传图片到微信服务器发生异常", ex.getMessage());
            throw new StoreSaasMarketingException("上传图片到微信服务器发生异常");
        }

    }

    /**
     * 转换文件名索引
     *
     */
    private String changeIndexName(String indexName){
        if(UploadImgEnum.USER.getCode().equalsIgnoreCase(indexName)){
            indexName = UploadImgEnum.USER.getCode();
        }
        if(UploadImgEnum.MERCHANT.getCode().equalsIgnoreCase(indexName)){
            indexName = UploadImgEnum.MERCHANT.getCode();
        }
        if(UploadImgEnum.PRODUCT.getCode().equalsIgnoreCase(indexName)){
            indexName = UploadImgEnum.PRODUCT.getCode();
        }
        if(UploadImgEnum.PROMOTION.getCode().equalsIgnoreCase(indexName)){
            indexName = UploadImgEnum.PROMOTION.getCode();
        }
        if(UploadImgEnum.ORDER.getCode().equalsIgnoreCase(indexName)){
            indexName = UploadImgEnum.ORDER.getCode();
        }
        if(UploadImgEnum.AFTERSALES.getCode().equalsIgnoreCase(indexName)){
            indexName = UploadImgEnum.AFTERSALES.getCode();
        }
        if(UploadImgEnum.FINANCE.getCode().equalsIgnoreCase(indexName)){
            indexName = UploadImgEnum.FINANCE.getCode();
        }
        if(UploadImgEnum.OTHERS.getCode().equalsIgnoreCase(indexName)){
            indexName = UploadImgEnum.OTHERS.getCode();
        }
        return indexName;
    }


    /**
     * 上传文件到微信服务器
     * @param bytes
     * @param fileName
     * @return
     */
    public String uploadFileToWx(byte[] bytes, String fileName, String indexName){
        try {
            File file = new File(fileName);
            FileOutputStream baseFos = new FileOutputStream(file);
            baseFos.write(bytes);
            String address = cosService.upload(file, indexName);
            String imgPath = cosService.generateUrl(address, false);
            baseFos.close();
            file.delete();
            return URLDecoder.decode(imgPath, "UTF-8");
        } catch (FileNotFoundException e) {
            throw new StoreSaasMarketingException("上传图片到微信服务器发生异常");
        } catch (Exception e) {
            throw new StoreSaasMarketingException("上传图片到微信服务器发生异常");
        }
    }

    /**
     * 上传文件到微信服务器
     * @param
     * @param
     * @return
     */
    public String uploadFileToWx(byte[] img, String indexName){
        try {
            String fileName = UUID.randomUUID()  + ".jpeg";
            File file = new File(fileName);
            FileOutputStream fis = new FileOutputStream(file);
            fis.write(img);
            String address = cosService.upload(file, indexName.concat(fileName));
            String imgPath = cosService.generateUrl(address, false);
            return URLDecoder.decode(imgPath, "UTF-8");
        } catch (FileNotFoundException e) {
            throw new StoreSaasMarketingException("上传图片到微信服务器发生异常");
        } catch (Exception e) {
            throw new StoreSaasMarketingException("上传图片到微信服务器发生异常");
        }
    }

}
