package com.tuhu.store.saas.marketing.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Create by wangshuai2 on 2018/11/28 18:47
 */
public class DataTimeUtil {

    /**
     * 取指定日期零点
     *
     * @return
     */
    public static Date getDateStartTime(Date date) {
        Calendar calendar = new Calendar.Builder().setInstant(date).build();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 取指定日期零点
     *
     * @return
     */
    public static Date getDateZeroTime(Date date) {
        Calendar calendar = new Calendar.Builder().setInstant(date).build();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 取指定日期上个月零点
     *
     * @return
     */
    public static Date getLastOneMounthTime(Date date) {
        Calendar calendar = new Calendar.Builder().setInstant(date).build();
        calendar.add(Calendar.DATE, -30);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 取指定日期上个月零点
     *
     * @return
     */
    public static Date getNextMounthTime(Date date) {
        Calendar calendar = new Calendar.Builder().setInstant(date).build();
        calendar.add(Calendar.DATE, 30);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     * 获取指定日期相加指定天数后的日期
     *
     * @return
     */
    public static Date getDateByAddDayOfMonth(Date date, Integer dayOfMonth) {
        if (null == date || null == dayOfMonth) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, dayOfMonth.intValue());
        return c.getTime();
    }

    public static void main(String[] args) {
        DataTimeUtil dataTimeUtil = new DataTimeUtil();
        System.out.println(dataTimeUtil.getLastOneMounthTime(new Date()));
        System.out.println(dataTimeUtil.getNextMounthTime(new Date()));
    }
}
