package com.obiscr.chatgpt.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author Wuzi
 */
public class StringUtil {

    public static String parse(String source) {
        if (source == null || source.length() <= 6){
            return HtmlUtil.create(source);
        }
        source = source.substring(5);
        if ("{}".equals(source.trim())) {
            return "# Ops\n" +
                    "It looks like something went wrong, no data was response.";
        }
        JSONObject object = JSON.parseObject(source);
        JSONArray resultArray = object.getJSONObject("message").getJSONObject("content").getJSONArray("parts");
        StringBuilder sb = new StringBuilder();
        for (Object s : resultArray) {
            sb.append(s.toString());
        }
        return sb.toString();
    }
}