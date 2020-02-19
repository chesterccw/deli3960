package com.chesterccw.attender.entity;

/**
 * @author chesterccw
 * @date 2020/2/13
 */
public class ExactDate {

    /**
     * 早上打卡时间
     */
    private String morning;

    /**
     * 晚上打卡时间
     */
    private String evening;

    public String getMorning() {
        return morning;
    }

    public void setMorning(String morning) {
        this.morning = morning;
    }

    public String getEvening() {
        return evening;
    }

    public void setEvening(String evening) {
        this.evening = evening;
    }

    public ExactDate() {

    }

    public ExactDate(String morning, String evening) {
        this.morning = morning;
        this.evening = evening;
    }
}
