package com.dailynews.lin.dailynews.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.dailynews.lin.dailynews.R;
import com.dailynews.lin.dailynews.Utils.CameraAlbumUtil;
import com.dailynews.lin.dailynews.Utils.ImageLoaderUtil;
import com.dailynews.lin.dailynews.Utils.PermissionUtil;
import com.dailynews.lin.dailynews.base.BaseActivity;
import com.dailynews.lin.dailynews.fragment.FavoriteFragment;
import com.dailynews.lin.dailynews.fragment.LocalFragment;
import com.dailynews.lin.dailynews.fragment.NewsFragment;
import com.dailynews.lin.dailynews.fragment.NewsListWebFM;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;

import com.dailynews.lin.dailynews.cn.sharesdk.onekeyshare.OnekeyShare;
import com.trycatch.mysnackbar.TSnackbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {
    private NewsFragment fragmentNews;
    private FavoriteFragment fragmentFavorite;
    private LocalFragment fragmentLocal;
    private CircleImageView iv_heaPic;
    private CameraAlbumUtil util;
    private TextView tv_name;
    @Bind(R.id.mToolbar)
    Toolbar mToolbar;
    @Bind(R.id.activity_main)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav)
    NavigationView navigationView;
    static Bitmap bitmap;
    String UserName;
    String res;
    View headerLayout;
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        util = new CameraAlbumUtil(this);
        SharedPreferences sp = getSharedPreferences("isFirstRunnings", Context.MODE_PRIVATE);
        UserName = sp.getString("UserName", "");
        res = sp.getString("res", "");
        getinformation();
        itinToolbar();
        initNavigation();
        initHeaderLayout();
        changFragment(new NewsFragment());
    }

    public void getinformation() {
        com.dailynews.lin.dailynews.Utils.ImageLoaderUtil imageLoaderUtil = new ImageLoaderUtil(this);
        bitmap = imageLoaderUtil.loadImage(res, new ImageLoaderUtil.OnLoadImageListener() {
            @Override
            public void onImageLoadOK(String url, Bitmap bitmap) {
                headerLayout = navigationView.getHeaderView(0);
                iv_heaPic = (CircleImageView) headerLayout.findViewById(R.id.iv_heaPic);
                iv_heaPic.setImageBitmap(bitmap);
                tv_name = (TextView) headerLayout.findViewById(R.id.tv_name);
                tv_name.setText(UserName);
            }

            @Override
            public void onImageLoadError(String url) {
            }
        });
        headerLayout = navigationView.getHeaderView(0);
        iv_heaPic = (CircleImageView) headerLayout.findViewById(R.id.iv_heaPic);
        iv_heaPic.setImageBitmap(bitmap);
        tv_name = (TextView) headerLayout.findViewById(R.id.tv_name);
        tv_name.setText(UserName);
    }

    private void itinToolbar() {
        mToolbar.setTitle("");//设置文本为空的
        setSupportActionBar(mToolbar);//调用此方法之后可以使用actionbar的所有功能
        ActionBar actionBar = getSupportActionBar();//通过上面设置的actionbar获取出来
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//Display：展示显示 HomeAsUp：左边的按钮 此条属性就是设置左边按钮能否被显示
            actionBar.setHomeAsUpIndicator(R.mipmap.home_as_up);//设置HomeAsUp的图片
        }
    }

    private void initHeaderLayout() {
        headerLayout = navigationView.getHeaderView(0);
        iv_heaPic = (CircleImageView) headerLayout.findViewById(R.id.iv_heaPic);
        iv_heaPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDagilog();
            }
        });
    }

    private void chooseDagilog() {
        new AlertDialog.Builder(this)
                .setTitle("选择头像")
                .setPositiveButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtil.requestPermissions(MainActivity.this, 111, new String[]{Manifest.permission.CAMERA}, new PermissionUtil.OnRequestPermissionListener() {
                            @Override
                            public void onRequestGranted() {
                                util.takePhoto();
                            }
                            @Override
                            public void onRequestDenied() {
                                TSnackbar.make(mToolbar, "权限被拒绝", TSnackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                })

                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtil.requestPermissions(MainActivity.this, 111, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionUtil.OnRequestPermissionListener() {
                            @Override
                            public void onRequestGranted() {
                                util.openAlbum();
                            }

                            @Override
                            public void onRequestDenied() {
                                Toast.makeText(MainActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .show();
    }

    @Override//当选项菜单被创建的时候
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolber, menu);//把toolbarxml文件加载
        return true;//让菜单显示
    }

    @Override//当选项菜单具体某个item被选中的时候
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                TSnackbar.make(mToolbar, "刷新", TSnackbar.LENGTH_LONG).show();
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_demo);
                Toolbar toolbar = (Toolbar) findViewById(R.id.mToolbar);
                View view = toolbar.findViewById(R.id.action_refresh);
                view.startAnimation(animation);
                break;
            case R.id.action_search:
                break;
            case R.id.action_more:
                ShareSDK.initSDK(this);
                showShare();
                break;
            case android.R.id.home://android下面的键的名字，一直是这个名字，但是要用android点
                drawerLayout.openDrawer(Gravity.LEFT);//打开抽屉布局,因为xml是gravity属性，所以需要设置Gravity下面的属性
                break;
        }
        return true;
    }

    private void initNavigation() {
        navigationView.setCheckedItem(R.id.News);//头条设置的灰色，一旦进去之后自己就默认为灰色
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.News:
                        if (fragmentNews == null) {
                            fragmentNews = new NewsFragment();
                        }
                        changFragment(fragmentNews);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.Favorite:
                        if (fragmentFavorite == null) {
                            fragmentFavorite = new FavoriteFragment();
                        }
                        changFragment(fragmentFavorite);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.Local:
                        if (fragmentLocal == null) {
                            fragmentLocal = new LocalFragment();
                        }
                        changFragment(fragmentLocal);
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });
    }

    public void changFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //加入返回栈
        if (fragment instanceof NewsListWebFM) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.fl_commcontent_main, fragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = CameraAlbumUtil.onActivityResult(requestCode, resultCode, data);
        iv_heaPic.setImageBitmap(bitmap);
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }
}