package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecordExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MarketingSendRecordMapper {

    int countByExample(MarketingSendRecordExample example);

    int deleteByExample(MarketingSendRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MarketingSendRecord record);

    int insertSelective(MarketingSendRecord record);

    List<MarketingSendRecord> selectByExample(MarketingSendRecordExample example);

    MarketingSendRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MarketingSendRecord record,
                                 @Param("example") MarketingSendRecordExample example);

    int updateByExample(@Param("record") MarketingSendRecord record,
                        @Param("example") MarketingSendRecordExample example);

    int updateByPrimaryKeySelective(MarketingSendRecord record);

    int updateByPrimaryKey(MarketingSendRecord record);
}
