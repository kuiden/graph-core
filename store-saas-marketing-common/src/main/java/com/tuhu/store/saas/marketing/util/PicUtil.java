package com.tuhu.store.saas.marketing.util;

import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import net.coobird.thumbnailator.Thumbnails;
//import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author xuqianwei
 * @title: PicUtil
 * @Description: 图片处理工具
 * @date 2020/1/17 14:38
 */
public class PicUtil {

    /**
     * 将文件流转换成byte数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @return 压缩质量后的图片字节数组
     */
    public static byte[] compressPicForScale(byte[] imageBytes) {
        long srcSize = imageBytes.length;
        if (imageBytes == null || imageBytes.length <= 0) {
            return imageBytes;
        }
        double accuracy = getAccuracy(srcSize / 1024);
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
            Thumbnails.of(inputStream)
                    .scale(accuracy)
                    .outputQuality(accuracy)
                    .toOutputStream(outputStream);
            imageBytes = outputStream.toByteArray();

        } catch (Exception e) {
            throw new StoreSaasMarketingException("压缩图片失败");
        }
        return imageBytes;
    }

    /**
     * 自动调节精度(经验数值)
     *
     * @param size 源图片大小
     * @return 图片压缩质量比
     */
    private static double getAccuracy(long size) {
        double accuracy;
        if (size < 900) {
            accuracy = 0.85;
        } else if (size < 2047) {
            accuracy = 0.6;
        } else if (size < 3275) {
            accuracy = 0.44;
        } else {
            accuracy = 0.4;
        }
        return accuracy;
    }


}
