package com.chesterccw.attender.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author chesterccw
 * @date 2020/1/11
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format 格式
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || "null".equals(seconds)){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.parseLong(seconds)));
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date,String format){
        try {
            if(format == null || format.isEmpty()){
                format = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date 字符串日期
     * @return
     */
    public static Long date2TimeStamp(String date){
        try {
            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断某一天是否是日期
     * @param date 格式: yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static boolean isDate(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long timestamp = sdf.parse(date).getTime();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取某个月的上一个月
     * @param date 格式: yyyy-MM-dd
     * @return string
     */
    public static String getLastMonth(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date));
        } catch (ParseException e) {
            c.setTime(new Date());
        }
        c.add(Calendar.MONTH, - 1);
        Date m = c.getTime();
        return format.format(m);
    }

    /**
     * 获取某个月的下一个月
     * @param date 格式: yyyy-MM-dd
     * @return string
     */
    public static String getNextMonth(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date));
        } catch (ParseException e) {
            c.setTime(new Date());
        }
        c.add(Calendar.MONTH, + 1);
        Date m = c.getTime();
        return format.format(m);
    }

    /**
     * 判断某年的某个月有多少天
     * @param date 格式: yyyy-MM-dd
     * @return int
     */
    public static int daysInMonth(String date){

        int days = -1;
        int year = Integer.parseInt(date.split("-")[0]);
        int month = -2;
        if(date.split("-")[1].startsWith("0")){
            month = Integer.parseInt(date.split("-")[1].substring(1));
        } else {
            month = Integer.parseInt(date.split("-")[1]);
        }

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                days = 30;
                break;
            case 2:
                if (year % 4 == 0 && year % 100 != 0 || year %400 == 0) {
                    days = 29;
                }else {
                    days = 28;
                }
                break;
            default:
                break;
        }
        return days;
    }

    /**
     * 获取某个月最后的几天
     * @param date 月份
     * @param days 天数
     * @return List<String>
     * @throws ParseException 格式解析异常
     */
    public static List<String> getLastDays(String date, int days) {
        Calendar calendar = Calendar.getInstance();
        try{
            if(date != null){
                calendar.setTime(df.parse(date));
            }
        } catch (ParseException e){
            return null;
        }
        List<String> list = new LinkedList<>();
        for(int i = days; i >= 1; i--){
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DATE, 1);
            calendar.add(Calendar.DATE, -i);
            String s = df.format(calendar.getTime());
            list.add(s);
        }
        return list;
    }

    /**
     * 获取某个月开始的几天
     * @param date 月份
     * @param days 天数
     * @return List<String>
     * @throws ParseException 格式解析异常
     */
    public static List<String> getFirstDays(String date, int days) {
        Calendar calendar = Calendar.getInstance();
        try{
            if(date != null){
                calendar.setTime(df.parse(date));
            }
        } catch (ParseException e){
            return null;
        }
        List<String> list = new LinkedList<>();
        for(int i = 0; i < days; i++){
            calendar.set(Calendar.DATE, 1);
            calendar.add(Calendar.DATE, +i);
            String s = df.format(calendar.getTime());
            list.add(s);
        }
        return list;
    }

    /**
     * 获取某个月的每天的日期
     * @param date 月份
     * @return List<String>
     */
    public static List<String> getCurrentDays(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try{
            if(date != null){
                calendar.setTime(df.parse(date));
            } else {
                throw new ParseException("日期格式异常",0);
            }
        } catch (ParseException e){
            return null;
        }
        List<String> list = new LinkedList<>();
        int days = DateUtil.daysInMonth(date);
        for(int i = 0; i < days; i++){
            calendar.set(Calendar.DATE, 1);
            calendar.add(Calendar.DATE, +i);
            String s = df.format(calendar.getTime());
            list.add(s);
        }
        return list;
    }

    /**
     * 判断某一天是否是周末
     * @param bDate 需要判断的日期，格式: yyyy-MM-dd
     * @return boolean
     */
    public static boolean isWeekend(String bDate) {
        Date date = null;
        try {
            date = df.parse(bDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}
