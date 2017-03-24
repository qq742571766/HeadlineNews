package com.dailynews.lin.dailynews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


public class NewsFragmentAdapter extends FragmentPagerAdapter {
    private List<String> mTitles;//tab便签要显示的文字
    private List<Fragment> mFragments;//ViewPager装Fragment的容器

    public void addmFragments(List<Fragment> mFragments) {
        this.mFragments = mFragments;
    }

    public void addmTitles(List<String> mTitles) {
        this.mTitles = mTitles;
    }

    public NewsFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
