package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;


import com.tuhu.store.saas.marketing.dataobject.CardTemplateItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 卡模板所属服务项目 Mapper 接口
 * </p>
 *
 * @author sunkuo
 * @since 2018-11-20
 */
public interface CardTemplateItemMapper {

    void insertCardTemplateItemList(List<CardTemplateItem> cardTemplateItemList);

    int del(@Param("id") long cardTemplateId);

    List<CardTemplateItem> selectCardTemplateItemList(@Param("id") long cardTemplateId);
    //  List<CardTemplateItem> selectCardTemplateItemList(CardTemplateItemCondition cardTemplateItemCondition);

    // void deleteCardTemplateItem(CardTemplateItemCondition cardTemplateItemCondition);

}
