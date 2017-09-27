package com.example.yj.util;

import com.example.yj.model.recommand.RecommandBodyValue;

import java.util.ArrayList;

/**
 * Created by yj on 2017/9/26.
 */

public class Util {

    //为ViewPager结构化数据,本次json中以@分割
    public static ArrayList<RecommandBodyValue> handleData(RecommandBodyValue value) {
        ArrayList<RecommandBodyValue> values = new ArrayList<>();
        String[] titles = value.title.split("@");
        String[] infos = value.info.split("@");
        String[] prices = value.price.split("@");
        String[] texts = value.text.split("@");
        ArrayList<String> urls = value.url;
        int start = 0;
        for (int i = 0; i < titles.length; i++) {
            RecommandBodyValue tempValue = new RecommandBodyValue();
            tempValue.title = titles[i];
            tempValue.info = infos[i];
            tempValue.price = prices[i];
            tempValue.text = texts[i];
            tempValue.url = extractData(urls, start, 3);
            start += 3;

            values.add(tempValue);
        }
        return values;
    }
    //提取信息
    private static ArrayList<String> extractData(ArrayList<String> source, int start, int interval) {
        ArrayList<String> tempUrls = new ArrayList<>();
        for (int i = start; i < start + interval; i++) {
            tempUrls.add(source.get(i));
        }
        return tempUrls;
    }
}