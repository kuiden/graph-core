package com.tuhu.store.saas.marketing.service.activity;

import com.tuhu.store.saas.marketing.service.activity.handler.MarketingComHandler;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>
 *  营销工厂类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-10
 */
@Component
public class MarketingFactory implements ApplicationContextAware {

    private static Map<String, MarketingComHandler> marketingBeanMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MarketingComHandler> map = applicationContext.getBeansOfType(MarketingComHandler.class);
        marketingBeanMap = new HashedMap();
        map.forEach((key, value) -> marketingBeanMap.put(value.getMarketingMethod(), value));
    }

    public static <T extends MarketingComHandler> T getMarketingComHandler(String marketingMethod) {
        return (T) marketingBeanMap.get(marketingMethod);
    }

}
