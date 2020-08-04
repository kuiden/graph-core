package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;
import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocalExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 本地(租户/用户)消息模板 Mapper 接口
 * </p>
 *
 * @author xuechaofu
 * @since 2018-11-13
 */
public interface MessageTemplateLocalMapper {

    int countByExample(MessageTemplateLocalExample example);

    int deleteByExample(MessageTemplateLocalExample example);

    int deleteByPrimaryKey(String id);

    int insert(MessageTemplateLocal record);

    int insertSelective(MessageTemplateLocal record);

    List<MessageTemplateLocal> selectByExampleWithBLOBs(MessageTemplateLocalExample example);

    List<MessageTemplateLocal> selectByExample(MessageTemplateLocalExample example);

    MessageTemplateLocal selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") MessageTemplateLocal record,
                                 @Param("example") MessageTemplateLocalExample example);

    int updateByExampleWithBLOBs(@Param("record") MessageTemplateLocal record,
                                 @Param("example") MessageTemplateLocalExample example);

    int updateByExample(@Param("record") MessageTemplateLocal record,
                        @Param("example") MessageTemplateLocalExample example);

    int updateByPrimaryKeySelective(MessageTemplateLocal record);

    int updateByPrimaryKeyWithBLOBs(MessageTemplateLocal record);

    int updateByPrimaryKey(MessageTemplateLocal record);

    MessageTemplateLocal selectByIdAndTenantId(@Param("id") String id, @Param("tenantId") Long tenantId);

}
