package com.dailynews.lin.dailynews.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.adapter.NewsFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class NewsFragment extends Fragment {
    private View view;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<String> mTitles;//tab便签要显示的文字
    private List<Fragment> mFragments;//ViewPager装Fragment的容器
    private String[] mTypes = {"top", "shehui", "guonei", "guoji", "yule", "tiyu", "junshi", "keji", "caijing", "shishang"};
    private NewsFragmentAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_newsfragment, null);
        initWidget();
        initData();
        return view;
    }

    //初始化控件
    private void initWidget() {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_newsfragment);
        mTabLayout = (TabLayout) view.findViewById(R.id.tablayout_newsfragment);
    }

    private void initData() {
        mTitles = new ArrayList<>();
        mTitles.add("头条");
        mTitles.add("社会");
        mTitles.add("国内");
        mTitles.add("国际");
        mTitles.add("娱乐");
        mTitles.add("体育");
        mTitles.add("军事");
        mTitles.add("科技");
        mTitles.add("财经");
        mTitles.add("时尚");
        mFragments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            NewsListFragment fragment = new NewsListFragment();
            //Fragment对象可以带一个Bundle对象
            Bundle bundle = new Bundle();
            bundle.putString("type", mTypes[i]);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }
        //适配viewpager
        mAdapter = new NewsFragmentAdapter(getFragmentManager());
        mAdapter.addmTitles(mTitles);
        mAdapter.addmFragments(mFragments);
        mViewPager.setAdapter(mAdapter);
        //将TabLayout于ViewPager关联
        mTabLayout.setupWithViewPager(mViewPager);
    }
}