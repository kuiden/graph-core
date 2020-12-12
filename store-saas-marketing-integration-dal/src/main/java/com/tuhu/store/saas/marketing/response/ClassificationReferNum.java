package com.tuhu.store.saas.marketing.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @time 2020-12-12
 * @auther kudeng
 */
@Data
public class ClassificationReferNum implements Serializable {

    private static final long serialVersionUID = 1l;

    /**
     * 秒杀活动模板分类id
     */
    private String classificationId;

    private Integer num;

}
