package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.CrdCardItem;
import com.tuhu.store.saas.marketing.dataobject.CrdCardItemExample;
import java.util.List;

import com.tuhu.store.saas.marketing.request.card.QueryAvailableItemsReq;
import org.apache.ibatis.annotations.Param;

public interface CrdCardItemMapper {
    long countByExample(CrdCardItemExample example);

    int deleteByExample(CrdCardItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CrdCardItem record);

    int insertSelective(CrdCardItem record);

    List<CrdCardItem> selectByExample(CrdCardItemExample example);

    CrdCardItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CrdCardItem record, @Param("example") CrdCardItemExample example);

    int updateByExample(@Param("record") CrdCardItem record, @Param("example") CrdCardItemExample example);

    int updateByPrimaryKeySelective(CrdCardItem record);

    int updateByPrimaryKey(CrdCardItem record);

    List<CrdCardItem> selectAvailableItems(QueryAvailableItemsReq req);

    List<CrdCardItem> crdCardItemsByCardIds(@Param("cardIds")List<Long> cardIds);
}