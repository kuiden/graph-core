/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.config.elk.layout;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import com.alibaba.fastjson.JSON;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ksewen
 * @date 2018/12/212:10 PM
 */
public class LogstashLayout extends LayoutBase<ILoggingEvent> {

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static ThrowableProxyConverter converter = new ThrowableProxyConverter();

    protected String machineName;

    protected String localAddress;

    static {
        converter.setOptionList(Collections.singletonList("full"));
        converter.start();
    }

    public LogstashLayout() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            machineName = address.getHostName();
            localAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
            this.addError("LogstashLayout无法获取machineName和localAddress", e);
        }
    }

    @Override
    public String doLayout(ILoggingEvent iLoggingEvent) {
        return buildElkFormat(iLoggingEvent);
    }

    private String buildElkFormat(ILoggingEvent iLoggingEvent) {
        Map<String, Object> layout = new LinkedHashMap<>();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(iLoggingEvent.getTimeStamp()),
                ZoneId.systemDefault());

        layout.put("machineName", machineName);
        layout.put("localAddress", localAddress);
        layout.put("threadName", iLoggingEvent.getThreadName());
        layout.put("level", iLoggingEvent.getLevel().toString());
        layout.put("time", dateTimeFormatter.format(localDateTime));
        layout.put("message", iLoggingEvent.getFormattedMessage());
        layout.put("logger", iLoggingEvent.getLoggerName());

        writeMdc(layout, iLoggingEvent);
        writeThrowable(layout, iLoggingEvent);
        Map<String, String> propertyMap = iLoggingEvent.getLoggerContextVO().getPropertyMap();
        if (null != propertyMap) {
            layout.put("applicationName", propertyMap.get("elkIdentifier"));
        }
        return JSON.toJSONString(layout);
    }

    private void writeMdc(Map<String, Object> layout, ILoggingEvent iLoggingEvent) {
        if (iLoggingEvent.getMDCPropertyMap() != null && iLoggingEvent.getMDCPropertyMap().size()>0) {
            layout.putAll(iLoggingEvent.getMDCPropertyMap());
        }
    }

    private void writeThrowable(Map<String, Object> layout, ILoggingEvent event) {
        IThrowableProxy iThrowableProxy = event.getThrowableProxy();
        if (iThrowableProxy != null && iThrowableProxy instanceof ThrowableProxy) {
            ThrowableProxy throwableProxy = (ThrowableProxy) iThrowableProxy;
            Throwable t = throwableProxy.getThrowable();
            Map<String, Object> throwable = new LinkedHashMap<>();

            throwable.put("message", t.getMessage());
            throwable.put("className", t.getClass().getCanonicalName());
            throwable.put("stackTrace", writeStackTrace(event));
            layout.put("throwable", throwable);
        }
    }

    private String writeStackTrace(ILoggingEvent event) {
        StringBuilder stringBuilder = new StringBuilder(2048);
        IThrowableProxy proxy = event.getThrowableProxy();
        if (proxy != null) {
            stringBuilder.append(converter.convert(event));
            stringBuilder.append(CoreConstants.LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }
}
