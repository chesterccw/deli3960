# deli3960
得力3960型打卡机考勤表解析工具 - Java

## 使用方法
获取考勤对象
```$java
    /**
     * 测试获取 Attender 对象
     * @see AttenderUtil#getDate(JSONArray)  
     */
    @Test
    public void getDate() {
        String path = this.getClass().getResource("/201810.xls").toString();
        ExcelReader reader = ExcelUtil.getReader(path);
        List<List<Object>> readAllAsList = reader.read();
        JSONArray array = JSON.parseArray(JSON.toJSONString(readAllAsList));
        Attender attender = AttenderUtil.getDate(array);
        attender.setMap(map);
        System.out.println(JSON.toJSON(attender));
    }
```

获取打卡信息
```$xslt
    /**
     * 测试获取所有员工打卡信息
     * @see AttenderUtil#getInfo(Map, JSONArray, int) 
     */
    @Test
    public void getInfo() {
        String path = this.getClass().getResource("/201810.xls").toString();
        ExcelReader reader = ExcelUtil.getReader(path);
        List<List<Object>> readAllAsList = reader.read();
        JSONArray array = JSON.parseArray(JSON.toJSONString(readAllAsList));
        map = AttenderUtil.getInfo(map,array,startIndex);
        Attender attender = Attender.getInstance();
        attender.setMap(map);
        AttenderUtil.output(attender);
    }
```
