package com.ayla.hotelsaas.utils;

import java.util.Arrays;
import java.util.Map;

public class SignUtils {

    public static String addsignParams(Map<String, String> param) {
        String[] keyarray = param.keySet().toArray(new String[0]);
        //拼接
        Arrays.sort(keyarray);
        StringBuilder str = new StringBuilder();
        for (String key : keyarray) {
            if (str.length() != 0) {
                str.append("&" + key + "=" + param.get(key));
            } else {
                str.append(key + "=" + param.get(key));
            }
        }
        return str.toString();
    }
}
