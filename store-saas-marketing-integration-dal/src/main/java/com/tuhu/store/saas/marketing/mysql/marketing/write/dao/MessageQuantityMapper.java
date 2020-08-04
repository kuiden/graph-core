package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.MessageQuantity;
import com.tuhu.store.saas.marketing.dataobject.MessageQuantityExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageQuantityMapper {

    int countByExample(MessageQuantityExample example);

    int deleteByExample(MessageQuantityExample example);

    int deleteByPrimaryKey(String id);

    int insert(MessageQuantity record);

    int insertSelective(MessageQuantity record);

    List<MessageQuantity> selectByExample(MessageQuantityExample example);

    MessageQuantity selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") MessageQuantity record,
                                 @Param("example") MessageQuantityExample example);

    int updateByExample(@Param("record") MessageQuantity record, @Param("example") MessageQuantityExample example);

    int updateByPrimaryKeySelective(MessageQuantity record);

    int updateByPrimaryKey(MessageQuantity record);
}
