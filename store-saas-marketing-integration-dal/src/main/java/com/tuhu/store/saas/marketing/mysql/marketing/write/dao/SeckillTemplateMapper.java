package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.base.mapper.BaseWriteMapper;
import com.tuhu.store.saas.marketing.dataobject.SeckillTemplate;
import com.tuhu.store.saas.marketing.response.ClassificationReferNum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 秒杀活动基础模板表 Mapper 接口
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public interface SeckillTemplateMapper extends BaseWriteMapper<SeckillTemplate> {

    List<ClassificationReferNum> getClassificaReferNum(@Param("tenantId") Long tenantId, @Param("ids") List<String> ids);

}
