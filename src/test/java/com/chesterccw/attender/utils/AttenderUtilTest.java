package com.chesterccw.attender.utils;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chesterccw.attender.entity.Attender;
import com.chesterccw.attender.entity.Employee;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chesterccw
 * @date 2020/2/13
 */
public class AttenderUtilTest {

    private Map<Integer, Employee> map = new LinkedHashMap<>();
    // 刷卡记录表路径
    private String path = "filePath";

    /**
     * 测试获取 Attender 对象
     * @see AttenderUtil#getDate(JSONArray)
     */
    @Test
    public void getDate() {
        ExcelReader reader = ExcelUtil.getReader(path);
        List<List<Object>> readAllAsList = reader.read();
        JSONArray array = JSON.parseArray(JSON.toJSONString(readAllAsList));
        Attender attender = AttenderUtil.getDate(array);
        attender.setMap(map);
        System.out.println(JSON.toJSON(attender));
    }


    /**
     * 测试获取所有员工打卡信息
     * @see AttenderUtil#getInfo(Map, JSONArray, int)
     */
    @Test
    public void getInfo() {
        ExcelReader reader = ExcelUtil.getReader(path);
        List<List<Object>> readAllAsList = reader.read();
        JSONArray array = JSON.parseArray(JSON.toJSONString(readAllAsList));
        int startIndex = 0;
        map = AttenderUtil.getInfo(map,array, startIndex);
        Attender attender = Attender.getInstance();
        attender.setMap(map);
        AttenderUtil.output(attender);
    }

}
