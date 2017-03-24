package com.dailynews.lin.dailynews.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyGuideAdapter extends PagerAdapter {
    //准备一个集合用来装所有的view
    private ArrayList<View> views = new ArrayList<View>();

    //添加数据到我的适配器
    public void addViewToAdapter(View view) {
        if (view != null) {
            views.add(view);
        }
    }

    //获取将要显示在ViewPager内的View数量
    @Override
    public int getCount() {
        return views.size();
    }

    //判断两个参数view和object是否有关联,是否由对象生成界面
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //当滑出去一个页面时，将滑出去的页面销毁
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        //从集合当中获取一个View
        View view = views.get(position);
        //从ViewGroup里移除
        container.removeView(view);
    }

    //实例化(加载)一个页面
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //从集合当中获取一个View
        View view = views.get(position);
        //添加到ViewGroup里
        container.addView(view);
        return view;
    }
}
