package com.dhl.pizer.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SeerParamUtil {

    public static JSONObject buildDestinations(String targetLocation, String operation, String key, String value) {
        JSONObject params = new JSONObject();
        params.put("locationName", targetLocation);
        params.put("operation", operation);

        JSONArray propertiesArr = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("value", value);
        propertiesArr.add(json);

        params.put("properties", propertiesArr);

        return params;
    }

}
