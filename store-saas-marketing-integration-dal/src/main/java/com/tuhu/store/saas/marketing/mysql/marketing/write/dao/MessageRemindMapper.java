package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.tuhu.store.saas.marketing.dataobject.MessageRemind;
import com.tuhu.store.saas.marketing.dataobject.MessageRemindExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 客户消息提醒 Mapper 接口
 * </p>
 *
 * @author xuechaofu
 * @since 2018-11-13
 */
public interface MessageRemindMapper {

    int countByExample(MessageRemindExample example);

    int deleteByExample(MessageRemindExample example);

    int deleteByPrimaryKey(String id);

    int insert(MessageRemind record);

    int insertSelective(MessageRemind record);

    List<MessageRemind> selectByExampleWithBLOBs(MessageRemindExample example);

    List<MessageRemind> selectByExample(MessageRemindExample example);

    MessageRemind selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") MessageRemind record, @Param("example") MessageRemindExample example);

    int updateByExampleWithBLOBs(@Param("record") MessageRemind record, @Param("example") MessageRemindExample example);

    int updateByExample(@Param("record") MessageRemind record, @Param("example") MessageRemindExample example);

    int updateByPrimaryKeySelective(MessageRemind record);

    int updateByPrimaryKeyWithBLOBs(MessageRemind record);

    int updateByPrimaryKey(MessageRemind record);

    /**
     * 批量插入客户消息提醒
     * @param list
     */
    void insertMessageRemindList(List<MessageRemind> list);

    /**
     * 批量更新客户提醒次数
     * @param remindIdList
     */
    void updateRemindTryTimes(@Param("list") List<String> remindIdList);

    /**
     * 更加id更新客户提醒状态
     * @param list
     * @param status
     */
    void updateStatusByIdList(@Param("list") List<String> list, @Param("status") String status);

    /**
     * 批量更新短信发送结果
     * @param messageRemindList
     */
    void appendSendStatusMessage(@Param("list") List<MessageRemind> messageRemindList);
}
