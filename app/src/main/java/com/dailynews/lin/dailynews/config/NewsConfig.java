package com.dailynews.lin.dailynews.config;

import com.dailynews.lin.dailynews.entity.NewsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/20.
 */

public class NewsConfig {
    public static ArrayList<NewsInfo> NewsCollectList = new ArrayList<>();

    public static String getNewsUrl(String newsType) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://v.juhe.cn/toutiao/index?type=");
        sb.append(newsType);
        sb.append("&key=881604939486629f0a452792fa0ee67b");
        return sb.toString();
    }
}
