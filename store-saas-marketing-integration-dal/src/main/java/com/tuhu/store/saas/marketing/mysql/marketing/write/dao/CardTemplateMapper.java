package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;


import com.tuhu.store.saas.marketing.dataobject.CardTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 卡模板 Mapper 接口
 * </p>
 *
 * @author sunkuo
 * @since 2018-11-20
 */
public interface CardTemplateMapper {

    int insertCardTemplate(CardTemplate cardTemplate);

    int updateCardTemplate(CardTemplate cardTemplate);

    Integer checkCardTemplateName(@Param("name") String cardName, @Param("id") Long id, @Param("tenantId") Long tenantId, @Param("storeId") Long storeId);

    void deleteCardTemplate(CardTemplate cardTemplate);

    CardTemplate getCardTemplateById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("storeId") Long storeId);

    List<CardTemplate> selectPage(@Param("status") String status,@Param("query") String query, @Param("tenantId") Long tenantId, @Param("storeId") Long storeId,@Param("isShow") Byte isShow);

    //  List<CardTemplate> queryCardTemplateList(CardTemplateCondition cardTemplateCondition);

    //  CardTemplate queryCardTemplate(CardTemplateCondition cardTemplateCondition);

    List<CardTemplate> selectCardTemplateByIds(@Param("cardTemplateIds") List<Long> cardTemplateIds);

}
