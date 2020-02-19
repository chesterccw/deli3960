package com.chesterccw.attender.entity;


import com.chesterccw.attender.utils.StringUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author chesterccw
 * @date 2020/2/13
 */
public class Employee {

    private static final int NO_STATUS = 0;
    private static final int MORNING = 1;
    private static final int EVENING = 2;

    /**
     * 员工id
     */
    private int id;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 起始行数
     */
    private int currentIndex;

    /**
     * 截止行数
     */
    private int nextIndex;

    /**
     * 存储员工的某天的打卡时间(早上和晚上)
     * key:2019-11-01
     * value:Time(morning = 08:56,evening = 21:12)
     */
    private Map<String, ExactDate> map = new LinkedHashMap<>();

    /**
     * 存储员工的数据，没有整理之前的
     */
    private List<String[]> dataList = new LinkedList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Map<String, ExactDate> getMap() {
        return map;
    }

    public void setMap(Map<String, ExactDate> map) {
        this.map = map;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    public Employee() {

    }

    public Employee(int id, String name, String department, int currentIndex, int nextIndex) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.currentIndex = currentIndex;
        this.nextIndex = nextIndex;
    }

    public Employee(int id, String name, String department, int currentIndex, int nextIndex, Map<String, ExactDate> map) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.currentIndex = currentIndex;
        this.nextIndex = nextIndex;
        this.map = map;
    }

    public List<String[]> getDataList() {
        return dataList;
    }

    public void setDataList(List<String[]> dataList) {
        this.dataList = dataList;
    }

    public Employee(int id, String name, String department, int currentIndex, int nextIndex, List<String[]> dataList) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.currentIndex = currentIndex;
        this.nextIndex = nextIndex;
        this.dataList = dataList;
    }

    /**
     * 获取员工某一天的打卡情况
     * @param date 日期
     * @return ExactDate
     */
    public ExactDate getTime(String date){
        return map.get(date);
    }

    /**
     * 获取员工的打卡天数
     * @return int
     */
    public int clockDays(){
        return map.size();
    }


    /**
     * 获取员工未打卡的日期
     * @param type {0|1|2}
     *             0: 不进行区分
     *             1: 上班未打卡
     *             2: 下班未打卡
     * @return List<String>
     */
    public List<String> getNoClockDate(int type){
        List<String> list = new LinkedList<>();
        if(type < 0 || type > 2){
            return list;
        }
        for(Map.Entry<String, ExactDate> entry : map.entrySet()){
            String date = entry.getKey();
            ExactDate exactDate = entry.getValue();
            switch (type){
                case NO_STATUS:
                    if(StringUtil.isEmpty(exactDate.getMorning()) || StringUtil.isEmpty(exactDate.getEvening())){
                        list.add(date);
                    }
                    break;
                case MORNING:
                    if(StringUtil.isEmpty(exactDate.getMorning())){
                        list.add(date);
                    }
                    break;
                case EVENING:
                    if(StringUtil.isEmpty(exactDate.getEvening())){
                        list.add(date);
                    }
                    break;
                default:
                    break;
            }
        }
        return list;
    }

}
