package com.netease.im;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.session.extension.BankTransferAttachment;
import com.netease.im.session.extension.RedPackageAttachement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dowin on 2017/5/16.
 */

public class ReactExtendsion {

    /**
     * @param readableArray
     * @param level
     * @param maxLevel
     * @return
     */
    public static List makeReadableArray2List(ReadableArray readableArray, int level, int maxLevel) {
        if (readableArray != null && readableArray.size() > 0) {
            List list = new ArrayList<>();
            for (int i = 0; i < readableArray.size(); i++) {
                ReadableType type = readableArray.getType(i);
                if (type == ReadableType.String) {
                    list.add(readableArray.getString(i));
                } else if (type == ReadableType.Array && level <= maxLevel) {
                    List child = makeReadableArray2List(readableArray.getArray(i), level + 1, maxLevel);
                    if (child != null)
                        list.add(child);
                } else if (type == ReadableType.Map && level <= maxLevel) {
                    Map childMap = makeReadableMap2HashMap(readableArray.getMap(i), level + 1, maxLevel);
                    if (childMap != null)
                        list.add(childMap);
                }
            }
            if (!list.isEmpty()) {
                return list;
            }
        }
        return null;
    }

    /**
     * @param readableMap
     * @param level
     * @param maxLevel
     * @return
     */
    public static Map makeReadableMap2HashMap(ReadableMap readableMap, int level, int maxLevel) {

        if (readableMap != null) {
            Map extendsionMap = new HashMap();
            ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();
                ReadableType type = readableMap.getType(key);
                if (type == ReadableType.String) {
                    extendsionMap.put(key, readableMap.getString(key));
                } else if (type == ReadableType.Array && level <= maxLevel) {
                    extendsionMap.put(key, makeReadableArray2List(readableMap.getArray(key), level + 1, maxLevel));
                } else if (type == ReadableType.Map && level <= maxLevel) {
                    extendsionMap.put(key, makeReadableMap2HashMap(readableMap.getMap(key), level + 1, maxLevel));
                }
            }
            if (!extendsionMap.isEmpty()) {
                return extendsionMap;
            }
        }
        return null;
    }

    public static WritableArray makeList2WritableArray(List list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        WritableArray array = Arguments.createArray();
        for (Object object : list) {
            if (object instanceof String) {
                array.pushString(String.valueOf(object));
            } else if (object instanceof ArrayList) {
                array.pushArray(makeList2WritableArray((List) object));
            } else if (object instanceof HashMap) {
                array.pushMap(makeHashMap2WritableMap((Map) object));
            }
        }
        return array;
    }

    public static WritableMap makeHashMap2WritableMap(Map map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        WritableMap writableMap = Arguments.createMap();
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof String) {
                writableMap.putString(key, String.valueOf(value));
            } else if (value instanceof ArrayList) {
                writableMap.putArray(key, makeList2WritableArray((List) value));
            } else if (value instanceof HashMap) {
                writableMap.putMap(key, makeHashMap2WritableMap((Map) value));
            }
        }
        return writableMap;
    }

    public static WritableMap createRedPackage(RedPackageAttachement attachement) {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("id",attachement.getRedPackageId());
        writableMap.putString("typeText",attachement.getTypeText());
        writableMap.putString("wishText",attachement.getWishText());
        writableMap.putString("type",attachement.getRedPackageType());
        return writableMap;
    }

    public static WritableMap createBankTransfer(BankTransferAttachment attachement) {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("id",attachement.getBankTransferId());
        writableMap.putString("typeText",attachement.getTypeText());
        writableMap.putString("explain",attachement.getExplain());
        writableMap.putString("value",attachement.getValue());
        return writableMap;
    }
}
