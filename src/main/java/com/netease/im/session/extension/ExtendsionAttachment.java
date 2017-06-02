package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dowin on 2017/5/15.
 */

public class ExtendsionAttachment extends CustomAttachment {

    private Map<String, Object> extendsion = new HashMap<>();
    private final static String KEY_RECENT_VALUE = "recentValue";
    private final static String KEY_EXTENDSION = "extendsion";
    private String recentValue;

    public ExtendsionAttachment(int type) {
        super(type);
    }

    public void setExtendsion(Map<String, Object> extendsion) {
        this.extendsion = extendsion;
    }

    public Map<String, Object> getExtendsion() {
        return extendsion;
    }

    public void setRecentValue(String recentValue) {
        this.recentValue = recentValue;
    }

    public String getRecentValue() {
        return recentValue;
    }

    @Override
    protected void parseData(JSONObject data) {

        recentValue = data.getString(KEY_RECENT_VALUE);

        JSONObject extendsionObject = data.getJSONObject(KEY_EXTENDSION);
        if (extendsionObject == null || extendsionObject.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, Object>> entrySet = extendsionObject.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof String) {
                extendsion.put(key, value);
            } else if (value instanceof JSONObject) {
                try {
                    extendsion.put(key, parseData2Map(extendsionObject.getJSONObject(key)));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else if (value instanceof JSONArray) {
                try {
                    extendsion.put(key, parseData2List(extendsionObject.getJSONArray(key)));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }

    }

    List parseData2List(JSONArray data) throws Throwable {
        if (data == null || data.isEmpty()) {
            return null;
        }
        int size = data.size();
        List list = new ArrayList();
        for (int i = 0; i < size; i++) {
            Object value = data.get(i);

            if (value instanceof String) {
                list.add(value);
            } else if (value instanceof JSONObject) {
                list.add(parseData2Map(data.getJSONObject(i)));
            } else if (value instanceof JSONArray) {
                list.add(parseData2List(data.getJSONArray(i)));
            }
        }
        return list;
    }

    Map<String, Object> parseData2Map(JSONObject data) throws Throwable {
        if (data == null || data.isEmpty()) {
            return null;
        }
        Set<Map.Entry<String, Object>> entrySet = data.entrySet();
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof JSONObject) {
                result.put(key, parseData2Map(data.getJSONObject(key)));
            } else if (value instanceof JSONArray) {
                result.put(key, parseData2List(data.getJSONArray(key)));
            }
        }
        return result;
    }

    @Override
    protected JSONObject packData() {
        JSONObject result = new JSONObject();
        result.put(KEY_RECENT_VALUE, recentValue);

        if (!extendsion.isEmpty()) {
            try {
                result.put(KEY_EXTENDSION, packData2Object(extendsion));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return result;
    }

    JSONArray packData2Array(List list) throws Throwable {
        if (list == null || list.isEmpty()) {
            return null;
        }
        JSONArray array = new JSONArray();
        for (Object object : list) {
            if (object instanceof String) {
                array.add(object);
            } else if (object instanceof ArrayList) {
                array.add(packData2Array((List) object));
            } else if (object instanceof HashMap) {
                array.add(packData2Object((Map<String, Object>) object));
            }
        }
        return array;
    }

    JSONObject packData2Object(Map<String, Object> map) throws Throwable {
        if (map == null || map.isEmpty()) {
            return null;
        }
        JSONObject result = new JSONObject();
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof ArrayList) {
                result.put(key, packData2Array((List) value));
            } else if (value instanceof HashMap) {
                result.put(key, packData2Object((Map<String, Object>) value));
            }
        }
        return result;
    }
}
