package com.tuhu.store.saas.marketing.service;

import javax.validation.groups.Default;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/31
 * Time: 11:13
 * Description:
 */
public interface ImageUploadService extends Default {
    String uploadImageByInputStream(InputStream inputStream, Long width, Long height);

    String uploadImageByBase64(String base64, Long width, Long height);
}