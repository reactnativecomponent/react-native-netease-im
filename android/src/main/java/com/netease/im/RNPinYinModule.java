package com.netease.im;

import android.text.TextUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.group.AbstractItem;
import com.netease.im.group.DefaultDataList;
import com.netease.im.group.DefaultGroupStrategy;
import com.netease.im.group.DefaultItem;
import com.netease.im.group.TextItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dowin on 2017/8/16.
 */

public class RNPinYinModule extends ReactContextBaseJavaModule {

    private final static String TAG = "PinYin";
    private final static String NAME = "PinYin";

    private ReactContext reactContext;

    public RNPinYinModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void sortPinYin(ReadableArray aa, String key, Promise promise) {

        if (aa != null && aa.size() > 0) {
            DefaultDataList dataList = new DefaultDataList(new DefaultGroupStrategy());
            for (int i = 0; i < aa.size(); i++) {
                ReadableType type = aa.getType(i);
                if (type == ReadableType.String) {
                    dataList.add(new TextItem(aa.getString(i)));
                } else if (type == ReadableType.Number) {
                    dataList.add(new TextItem(aa.getInt(i) + ""));
                } else if (type == ReadableType.Map) {
                    final String aKey = TextUtils.isEmpty(key) ? "key" : key;
                    ReadableMap map = aa.getMap(i);
                    if (map.hasKey(aKey)) {
                        ReadableType keyType = map.getType(aKey);
                        String name = "";
                        if (keyType == ReadableType.String) {
                            name = map.getString(aKey);
                        } else if (keyType == ReadableType.Number) {
                            name = map.getInt(aKey) + "";
                        }
                        DefaultItem item = new DefaultItem(name);
                        ReadableMapKeySetIterator iterator = map.keySetIterator();
                        WritableMap wMap = Arguments.createMap();
                        while (iterator.hasNextKey()) {
                            String nextKey = iterator.nextKey();
                            ReadableType nextType = map.getType(nextKey);
                            if (nextType == ReadableType.String) {
                                wMap.putString(nextKey, map.getString(nextKey));
                            } else if (nextType == ReadableType.Number) {
                                wMap.putString(nextKey, map.getInt(nextKey) + "");
                            }
                        }
                        item.setData(wMap);
                        dataList.add(item);
                    }
                }
            }
            dataList.build();

            Map<String, WritableArray> listHashMap = new HashMap<>();
            WritableMap writableMap = Arguments.createMap();
            int count = dataList.getCount();
            for (int i = 0; i < count; i++) {
                AbstractItem item = dataList.getItem(i);
                if (item instanceof DefaultItem) {
                    DefaultItem<WritableMap> defaultItem = (DefaultItem) item;

                    String belongs = defaultItem.belongsGroup();
                    WritableArray array = listHashMap.get(belongs);
                    if (array == null) {
                        array = Arguments.createArray();
                        listHashMap.put(belongs, array);
                    }
                    array.pushMap(defaultItem.getData());
                } else if (item instanceof TextItem) {
                    TextItem textItem = (TextItem) item;
                    String belongs = textItem.belongsGroup();
                    WritableArray array = listHashMap.get(belongs);
                    if (array == null) {
                        array = Arguments.createArray();
                        listHashMap.put(belongs, array);
                    }
                    array.pushString(textItem.getText());
                }
            }
            if (listHashMap.size() > 0) {
                for (Map.Entry<String, WritableArray> entry : listHashMap.entrySet()) {
                    writableMap.putArray(entry.getKey(), entry.getValue());
                }
            }
            listHashMap.clear();
            promise.resolve(writableMap);
        } else {
            promise.resolve(Arguments.createMap());
        }
    }
}
