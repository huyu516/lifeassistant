package com.hy.lifeassistant.base;

import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleAdapterFactory {

    public static SimpleAdapter create(Context context, int layoutId, int[] resIds, Object[]... allDatas) {
        List<Map<String, Object>> data = createDataList(allDatas);
        String[] dataKeys = createDataKeys(allDatas);
        return new SimpleAdapter(context, data, layoutId, dataKeys, resIds);
    }

    private static List<Map<String, Object>> createDataList(Object[]... allDatas) {
        List<Map<String, Object>> results = new ArrayList<>();

        int dimSize = allDatas.length;
        int itemSize = allDatas[0].length;

        for (int i = 0; i < itemSize; i++) {
            Map<String, Object> item = new HashMap<>();
            results.add(item);
        }

        for (int d = 0; d < dimSize; d++) {
            Object[] datas = allDatas[d];

            for (int i = 0; i < itemSize; i++) {
                Map<String, Object> item = results.get(i);
                item.put(String.valueOf(d), datas[i]);
            }
        }

        return results;
    }

    private static String[] createDataKeys(Object[]... allDatas) {
        String[] result = new String[allDatas.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = String.valueOf(i);
        }

        return result;
    }

}
