package com.dailynews.lin.dailynews.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.adapter.MyGuideAdapter;
import com.dailynews.lin.dailynews.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GuideActivity extends BaseActivity {
    @Bind(R.id.vp_guide)
    ViewPager mViewPager;
    @Bind({R.id.iv1_guide, R.id.iv2_guide, R.id.iv3_guide, R.id.iv4_guide})
    ImageView[] imgs = new ImageView[4];
    MyGuideAdapter mAdapter;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        //初始化ButterKnife（框架），将视图和控件进行绑定
        ButterKnife.bind(this);
        isFirstRunning();
        initWidget();
    }

    private void isFirstRunning() {
        //获得SharedPreferences对象，通过sp储存
        SharedPreferences sp = getSharedPreferences("isFirstRunning", Context.MODE_PRIVATE);
        //添加进Boolean值
        boolean isFirst = sp.getBoolean("isFirst", true);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isFirst", false);
        //提交
        editor.apply();
        if (!isFirst) {
            startActivity(LaunchActivity.class);
            this.finish();
        }
    }

    //找到控件，设置适配器
    private void initWidget() {
        mViewPager.addOnPageChangeListener(listener);
        mAdapter = new MyGuideAdapter();
        getViews();
        mViewPager.setAdapter(mAdapter);
    }

    public void getViews() {
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.item1_viewpager_guide, null);
        View view2 = inflater.inflate(R.layout.item2_viewpager_guide, null);
        View view3 = inflater.inflate(R.layout.item3_viewpager_guide, null);
        View view4 = inflater.inflate(R.layout.item4_viewpager_guide, null);
        mAdapter.addViewToAdapter(view1);
        mAdapter.addViewToAdapter(view2);
        mAdapter.addViewToAdapter(view3);
        mAdapter.addViewToAdapter(view4);
    }

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            for (ImageView img : imgs) {
                img.setImageResource(R.drawable.adware_style_default);
            }
            imgs[position].setImageResource(R.drawable.adware_style_selected);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        //当滑动状态改变时
        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                //滑动
                case ViewPager.SCROLL_STATE_DRAGGING:
                    flag = false;
                    break;
                //滑动完毕
                case ViewPager.SCROLL_STATE_SETTLING:
                    flag = true;
                    break;
                //什么都没做
                case ViewPager.SCROLL_STATE_IDLE:
                    if (mViewPager.getCurrentItem() == mAdapter.getCount() - 1 && !flag) {
                        startActivity(LaunchActivity.class);
                        GuideActivity.this.finish();
                    }
                    flag = true;
                    break;
            }
        }
    };
}