package com.chesterccw.attender.utils;

import com.alibaba.fastjson.JSONArray;
import com.chesterccw.attender.entity.Attender;
import com.chesterccw.attender.entity.Employee;
import com.chesterccw.attender.entity.ExactDate;

import java.util.*;

/**
 * @author chesterccw
 * @date 2020/2/13
 */
public class AttenderUtil {

    private static final int MIN_DAYS_IN_MONTH = 28;
    private static final int MAX_DAYS_IN_MONTH = 31;

    private static final String NEXT_LINE_SPLIT_MARK = "\\\\n";
    private static final String HOUR_MINUTE_SPLIT_MARK = ":";


    /**
     * 获取月信息
     * @param array 读取出的xls转化成的JSONArray
     * @return Attender
     */
    public static Attender getDate(JSONArray array){
        Attender attender = Attender.getInstance();
        String[] dateArray = array.get(1).toString().split(",");
        String date = dateArray[dateArray.length - 1];
        date = StringUtil.getStrIn(date,"\"","\"",new Integer[]{0,1});
        if(date != null){
            // 去掉中文 "考勤日期："
            date = StringUtil.deleteChineseChar(date);
            String start = date.split("～")[0];
            String end = date.split("～")[1];
            int days = DateUtil.daysInMonth(start);
            attender.setStart(start);
            attender.setEnd(end);
            attender.setDays(days);
        }
        return attender;
    }

    /**
     * 获取月信息
     * 首选寻找第一个具有 工号:X   姓名:XXX  部门:XXX   的行，记录其行号，(假设第一个行号是 5)
     * 紧接着寻找下一个具有如上特征的行，并且记录其行号 (假设第二个行号是 9)
     * 那么第一个人的信息就是 第5行开始到第8 (9-1)行，然后递归调用就可以读取所有员工的打卡信息
     * @param map 存放解析后的数据
     * @param array 读取出的xls转化成的JSONArray
     * @param index 拆分出来主要是为了方便递归
     * @return Map<Integer,Employee>
     */
    public static Map<Integer, Employee> getInfo(Map<Integer,Employee> map, JSONArray array, int index){
        Attender attender = getDate(array);
        if(attender == null) {
            return null;
        }
        int currentIndex = 0;
        int nextIndex = 0;
        String headLine = "";
        for(; index < array.size() ; index++){
            String currentHeadLine = array.get(index).toString();
            if(isHeader(currentHeadLine)){
                currentIndex = index;
                headLine = currentHeadLine;
                break;
            }
        }
        index++;
        for(; index < array.size() ; index++){
            String nextHeadLine = array.get(index).toString();
            if(isHeader(nextHeadLine)){
                nextIndex = index;
                break;
            }
            if(nextIndex != currentIndex && nextIndex == 0){
                nextIndex = array.size();
            }
        }
        if(isHeader(headLine)){
            String[] headLineArray = headLine.split(",");
            int id = Integer.parseInt(StringUtil.getStrInSemicolon(headLineArray[3]));
            String name = StringUtil.getStrInSemicolon(headLineArray[11]);
            String department = StringUtil.getStrInSemicolon(headLineArray[18]);
            List<String[]> dataList = new LinkedList<>();
            for(int i = currentIndex ; i <= nextIndex - 1 ; i++){
                dataList.add(StringUtil.getStrInSquareBrackets(array.get(i).toString()).split(","));
            }
            Employee employee = new Employee(id,name,department,currentIndex,nextIndex - 1,dataList);
            resolve(employee,attender.getYear(),attender.getMonth());
            dataList.clear();
            map.put(id,employee);
        }
        if(index < array.size()){
            getInfo(map, array, index);
        }
        return map;
    }

    /**
     * 解析某个员工的打卡情况
     * @param employee 员工对象
     * @param year 年
     * @param month 月
     */
    private static void resolve(Employee employee, int year, int month){
        if(employee == null || employee.getDataList().size() == 0){
            return;
        }
        Map<String, ExactDate> map = employee.getMap();
        List<String[]> dataList = employee.getDataList();
        int currentMonthDays = DateUtil.daysInMonth(getDate(year,month,"01"));
        if(currentMonthDays < MIN_DAYS_IN_MONTH || currentMonthDays > MAX_DAYS_IN_MONTH){
            return;
        }
        for(int i = 1 ; i <= currentMonthDays ; i++){
            String date = getDate(year,month,i);
            ExactDate exactDate = exactDate(i,dataList);
            if(exactDate != null){
                map.put(date,exactDate(i,dataList));
            }
        }
    }

    /**
     * 解析出打卡对象
     * @param day 天数
     * @param dataList 数据
     * @return ExactDate
     */
    private static ExactDate exactDate(int day, List<String[]> dataList){
        if(dataList.size() <= 0){
            return null;
        }
        Map<Integer,String> clockMap = getClockMap(day,dataList);
        if(clockMap.size() <= 0){
            return null;
        }
        String first = getFirstTime(clockMap);
        String last = getLastTime(clockMap);

        /*
         * 1、如果两个打卡时间都是空，算作当天没有上班
         * 2、如果上下班打卡时间相同(也就是说，只打了一次，要么上班没打卡，要么下班没打卡)
         *    此时要判断这个时间是在什么时候，如果是在早上或者下午，算下班没打卡。
         *    如果是在晚上或者凌晨，算上班没打卡。其实就是打卡异常，这个数据是需要人工去校对的
         * 3、正常打卡情况
         */
        if(isEmpty(first) && isEmpty(last)){
            return null;
        } else if (first.equals(last)){
            if(isMorning(first) || isAfternoon(first)){
                return new ExactDate(first,"");
            } else if (isEvening(last) || isBeforeMorning(last)) {
                return new ExactDate("",last);
            }
        }
        return new ExactDate(first,last);
    }

    /**
     * 获取某天的所有打卡时间
     * @param day 天数
     * @param dataList 数据
     * @return Map<Integer,String>
     */
    private static Map<Integer,String> getClockMap(int day,List<String[]> dataList){
        Map<Integer,String> map = new LinkedHashMap<>();
        if(dataList.size() <= 0 || day <= 0){
            return map;
        }
        int nextDay = day + 1;
        int index = 0;

        // 处理当天
        for (int i = 2 ; i < dataList.size() ; i++) {
            String[] strings = dataList.get(i);
            String current = strings[day];
            current = StringUtil.getStrInSemicolon(current);
            String[] currentArray = current.split(NEXT_LINE_SPLIT_MARK);
            for (String time : currentArray) {
                if (isEmptyString(time) && !isBeforeMorning(time)) {
                    map.put(++index, time);
                }
            }
        }

        if(nextDay <= MAX_DAYS_IN_MONTH){
            // 处理下一天
            for (int i = 2 ; i < dataList.size() ; i++) {
                String[] strings = dataList.get(i);
                String next = strings[nextDay];
                next = StringUtil.getStrInSemicolon(next);
                String[] nextArray = next.split(NEXT_LINE_SPLIT_MARK);
                for (String time : nextArray) {
                    if (isEmptyString(time) && isBeforeMorning(time)) {
                        map.put(++index, time);
                    }
                }
            }
        }
        return map;
    }

    /**
     * 获取当天第一次打卡
     * @param map 数据
     * @return String
     */
    private static String getFirstTime(Map<Integer,String> map){
        String first = "";
        for(Map.Entry<Integer,String> entry : map.entrySet()){
            String time = entry.getValue();
            if(!isBeforeMorning(time)){
                first = time;
                break;
            }
        }
        return first;
    }

    /**
     * 获取当天最后一次打卡
     * @param map 数据
     * @return String
     */
    private static String getLastTime(Map<Integer,String> map){
        String last = "";
        for(Map.Entry<Integer,String> entry : map.entrySet()){
            String time = entry.getValue();
            if(isMorning(time)){
                if(!time.equals(getFirstTime(map))){
                    last = time;
                }
            } else {
                last = time;
            }
        }
        return last;
    }

    /**
     * 判断字符串是否是空的
     * @param string string
     * @return boolean
     */
    private static boolean isEmptyString(String string){
        return !"".equals(string.replace(" ", ""));
    }

    /**
     * 判断时间是否在凌晨
     * @param time 格式 HH:mm
     * @return boolean
     */
    private static boolean isBeforeMorning(String time){
        String[] timeArray = time.split(HOUR_MINUTE_SPLIT_MARK);
        int hours = Integer.parseInt(timeArray[0]);
        return hours >= 0 && hours < 6;
    }

    /**
     * 判断时间是否在早晨
     * @param time 格式 HH:mm
     * @return boolean
     */
    private static boolean isMorning(String time){
        String[] timeArray = time.split(HOUR_MINUTE_SPLIT_MARK);
        int hours = Integer.parseInt(timeArray[0]);
        return hours >= 6 && hours < 12;
    }

    /**
     * 判断时间是否在下午
     * @param time 格式 HH:mm
     * @return boolean
     */
    private static boolean isAfternoon(String time){
        String[] timeArray = time.split(HOUR_MINUTE_SPLIT_MARK);
        int hours = Integer.parseInt(timeArray[0]);
        return hours >= 12 && hours < 18;
    }

    /**
     * 判断时间是否在晚上
     * @param time 格式 HH:mm
     * @return boolean
     */
    private static boolean isEvening(String time){
        String[] timeArray = time.split(HOUR_MINUTE_SPLIT_MARK);
        int hours = Integer.parseInt(timeArray[0]);
        return hours >= 18 && hours < 24;
    }

    /**
     * 重载方法
     * @param attender attender
     * @param day 天
     * @return String
     */
    private static String getDate(Attender attender,String day){
        return getDate(attender.getYear(),attender.getMonth(),day);
    }

    /**
     * 获取格式化之后的日期
     * @param year 年
     * @param month 月
     * @param day 日
     * @return String
     */
    private static String getDate(int year, int month, String day){
        if(Integer.parseInt(day) <= 9){
            return year + "-" + month + "-" + "0" + day;
        }
        return year + "-" + month + "-" + day;
    }

    /**
     * 重载方法
     * @param year 年
     * @param month 月
     * @param day 日
     * @return String
     */
    private static String getDate(int year, int month, int day){
        return getDate(year,month,String.valueOf(day));
    }

    /**
     * 判断某一个对象是否为空
     * @param object object
     * @return boolean
     */
    private static boolean isEmpty(Object object){
        return "".equals(object) || null == object || "\"\"".equals(object) || "null".equals(object);
    }

    /**
     * 判断是否是员工的标题行
     * @param current string
     * @return boolean
     */
    private static boolean isHeader(String current){
        return current.contains("工号") && current.contains("姓名") && current.contains("部门");
    }

    /**
     * 输出
     * e.g.
     *      -----~21:22 代表 上班未打卡，下班打卡时间是 21:22
     *      13:40~----- 代表 下班未打卡，上班打卡时间是 13:40
     * @param map 数据
     */
    private static void output(Map<Integer, Employee> map){
        if(map == null || map.size() == 0){
            return;
        }
        for(Map.Entry<Integer,Employee> employeeEntry : map.entrySet()){
            Employee employee = employeeEntry.getValue();
            System.out.println();
            System.out.println("ID:" + employee.getId() + "\t" + "姓名:" + employee.getName() + "\t" + "部门:" +
                    employee.getDepartment() + "\t" + "打卡天数:" + employee.getMap().size());
            Map<String, ExactDate> map1 = employee.getMap();

            for(Map.Entry<String, ExactDate> exactDateEntry : map1.entrySet()){
                System.out.print(exactDateEntry.getKey() + "\t");
            }

            System.out.println();

            for(Map.Entry<String, ExactDate> exactDateEntry : map1.entrySet()){
                ExactDate exactDate = exactDateEntry.getValue();
                if(exactDate != null){
                    if("".equals(exactDate.getMorning())){
                        exactDate.setMorning("-----");
                    }
                    if("".equals(exactDate.getEvening())){
                        exactDate.setEvening("-----");
                    }
                    System.out.print(exactDate.getMorning() + "~" + exactDate.getEvening() + "\t");
                } else {
                    System.out.print("\t");
                }
            }

            System.out.println();
        }
    }

    /**
     * 输出
     * @param attender object
     */
    public static void output(Attender attender){
        output(attender.getMap());
    }

}
