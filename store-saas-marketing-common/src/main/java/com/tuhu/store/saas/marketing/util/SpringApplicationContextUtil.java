package com.tuhu.store.saas.marketing.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringApplicationContextUtil implements ApplicationContextAware {
	
	private static ApplicationContext springContext;
	
	public SpringApplicationContextUtil() {
		super();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if(springContext==null) {
			springContext = applicationContext;
		}
	}

	public static ApplicationContext getApplicationContext() {
		return springContext;
	}
	
	public static String getContextId() {
		return springContext.getId();
	}

	public static <T> T getBean(Class<T> clazz){
		return springContext.getBean(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) springContext.getBean(name);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBeanByClassName(String className) {
		Class<T> clazz = null;
		try {
			clazz = (Class<T>) Class.forName(className);
		}catch (Exception e) {
			e.printStackTrace();
			log.error("class not fount"+className);
		}
		return springContext.getBean(clazz);
	}
	
	public static <T> T getBean(String string, Class<T> clazz) {
		return (T)springContext.getBean(string, clazz);
	}
}
