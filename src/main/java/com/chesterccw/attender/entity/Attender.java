package com.chesterccw.attender.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤表实体类
 * @author chesterccw
 * @date 2020/2/13
 */
public class Attender {

    /**
     * 列入当前的Attender是2019年11月份的记录
     * 则:
     * start = 2019-11-01
     * end   = 2019-11-30
     */

    /**
     * 当前月的开始日期
     */
    private String start;

    /**
     * 当前月的结束日期
     */
    private String end;

    /**
     * 当前月的天数
     */
    private int days;

    /**
     * 存储员工编号以及员工对象
     */
    private Map<Integer,Employee> map = new LinkedHashMap<>();

    /**
     * 用于数据返回，当通过姓名或者部门查找的时候，会用到此结构
     */
    private List<Employee> list = new ArrayList<>();

    private static Attender attender = null;

    private Attender() {

    }
    public static Attender getInstance(){
        if(attender == null){
            attender = new Attender();
        }
        return attender;
    }


    /**
     * 通过员工id获取某个员工的当前月的考勤记录
     * @param id 员工id
     * @return Employee
     */
    public Employee getInfoById(int id){
        return map.get(id);
    }

    /**
     * 通过员工姓名获取某些员工的当前月的考勤记录
     * @param name 员工姓名
     * @return List<Employee>
     */
    public List<Employee> getInfoByName(String name){
        list.clear();
        for(Map.Entry<Integer,Employee> entry : map.entrySet()){
            Employee employee = entry.getValue();
            if(name.equals(employee.getName())){
                list.add(employee);
            }
        }
        return list;
    }

    /**
     * 通过部门获取某个部门员工的当前月的考勤记录
     * @param department 部门名称
     * @return List<Employee>
     */
    public List<Employee> getInfoByDepartment(String department){
        list.clear();
        for(Map.Entry<Integer,Employee> entry : map.entrySet()){
            Employee employee = entry.getValue();
            if(department.equals(employee.getDepartment())){
                list.add(employee);
            }
        }
        return list;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Map<Integer, Employee> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Employee> map) {
        this.map = map;
    }

    public List<Employee> getList() {
        return list;
    }

    public void setList(List<Employee> list) {
        this.list = list;
    }

    public Attender(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public Attender(String start, String end, int days) {
        this.start = start;
        this.end = end;
        this.days = days;
    }

    public int getYear(){
        return Integer.parseInt(start.split("-")[0]);
    }

    public int getMonth(){
        return Integer.parseInt(start.split("-")[1]);
    }
}
