package com.chesterccw.attender.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author chesterccw
 * @date 2019/12/9
 */
public class StringUtil extends StringUtils {

    /**
     *
     * @param string
     * @param ch
     * @return
     */
    public static int countTimes(String string,String ch) {
        return (string.length() - string.replace(ch, "").length()) / ch.length();
    }

    /**
     * 截取两个字符串之间的字符
     * @param str   原始字符串
     * @param start 开始字符
     * @param end   结束字符
     * @param type  类型：一个长度为2的 Integer 数组，一共有以下四个值，主要是为了处理 字符串包行相同的 start 或 end 的情况
     *                  0 代表首位, 1 代表末位
     *                  [0,0]   代表 start 和 end 都截取首位
     *                  [0,1]   代表 start 截取首位, end 截取末位
     *                  [1,0]   代表 start 截取末位, end 截取首位
     *                  [1,1]   代表 start 和 end 都截取末位
     * @return String
     */
    public static String getStrIn(String str, String start, String end, Integer[] type) {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int startIndex = 0;
        int endIndex = 0;

        int type0 = type[0];
        int type1 = type[1];

        if(type0 == 0 && type1 ==0){
            startIndex = str.indexOf(start);
            endIndex = str.indexOf(end);
        } else if(type0 == 0 && type1 ==1) {
            startIndex = str.indexOf(start);
            endIndex = str.lastIndexOf(end);
        } else if(type0 == 1 && type1 ==0) {
            startIndex = str.lastIndexOf(start);
            endIndex = str.indexOf(end);
        } else if(type0 == 1 && type1 ==1) {
            startIndex = str.lastIndexOf(start);
            endIndex = str.lastIndexOf(end);
        }

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (startIndex < 0) {
            return null;
        }
        if (endIndex < 0) {
            return null;
        }

        /* 开始截取 */
        return str.substring(startIndex, endIndex).substring(start.length());
    }

    /**
     * 获取分号之间的字符串
     * @param string string
     * @return String
     */
    public static String getStrInSemicolon(String string){
        return getStrIn(string,"\"", "\"", new Integer[]{0, 1});
    }

    /**
     * 获取方括号"[","]"之间的字符串
     * @param string string
     * @return String
     */
    public static String getStrInSquareBrackets(String string){
        return getStrIn(string,"[", "]", new Integer[]{0, 1});
    }

    /**
     * 判断字符穿是否包含标点符号
     * @param s
     * @return
     */
    public static boolean containsPunctuationMark(String s) {
        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        return s.length() != tmp.length();
    }

    /**
     * 判断是否包含中文
     * @param c
     * @return boolean
     */
    public static boolean checkChineseChar(char c) {
        return (c >= '\u4e00' && c <= '\u9fa5') || (c >= '\uf900' && c <= '\ufa2d') || c == '\uFF1A';
    }

    /**
     * 删除字符串中的中文
     * @param source 源字符串
     * @return string
     */
    public static String deleteChineseChar(String source) {
        char[] cs = source.toCharArray();
        int length= cs.length;
        char [] buf = new char[length];
        for (int i = 0; i <length; i++) {
            char c = cs[i];
            if (!checkChineseChar(c)) {
                buf[i] = c;
            }
        }
        String ret = new String(buf);
        return ret.trim();
    }

    public static String getClassName(String className){
        return className.substring(className.lastIndexOf(".") + 1);
    }

}
