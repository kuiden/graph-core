package com.tuhu.store.saas.marketing.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhaochenchen
 * @date 2018/11/815:33
 */
@Component
public class IdKeyGen {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 序列号前缀，store-saas-PRODUCT:{tenantId}:{序列号名称}:
     */
    private static String SEQ_KEY_PREFIX = "store-saas-marketing:%s:%s:";

    /**
     * 默认的序列号名称
     */
    private static String DEFUALT_SEQ_KEY_NAME = "default_id";

    /**
     * product应用序列号名称
     */
    public static String ID_KEY_CRM = "store_saas_crm_id";

    /**
     * product应用序列号名称
     */
    public static String ID_KEY_USER = "store_saas_user_id";

    /**
     * 获取id，默认为第一个配置
     *
     * @param tenantId 租户id
     * @return 拼接好的id，时间戳+租户id+序列号
     */
    public String generateId(Long tenantId) {
        return this.generateId(tenantId, null);
    }

    /**
     * 通过配置文件中tuhu.finace.sequence.conf对应的最后一个字段获取id
     *
     * @param tenantId 租户id
     * @param seqName  序列号名称
     * @return 拼接好的id，时间戳+租户id+序列号
     */
    public String generateId(Long tenantId, String seqName) {
        String tenantIdStr = String.format("%06d", tenantId);
        long currentTimeMillis = System.currentTimeMillis();
        String currentTimeMillisStr = Long.toString(currentTimeMillis);
        if (StringUtils.isBlank(seqName)) {
            seqName = DEFUALT_SEQ_KEY_NAME;
        }
        String keyName = String.format(SEQ_KEY_PREFIX, tenantId, seqName);
        Long seqValue = this.next(keyName);
        return currentTimeMillisStr + tenantIdStr + seqValue;
    }

    public Long next(String key) {
        Long currentValue = stringRedisTemplate.opsForValue().increment(key, 1L);
        while (Long.valueOf(0).compareTo(currentValue) >= 0) {
            currentValue = stringRedisTemplate.opsForValue().increment(key, 1L);
        }
        return currentValue;
    }
}

