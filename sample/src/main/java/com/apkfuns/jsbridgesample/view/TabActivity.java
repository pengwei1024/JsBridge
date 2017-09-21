package com.apkfuns.jsbridgesample.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.apkfuns.jsbridgesample.R;
import com.apkfuns.jsbridgesample.view.base.BaseActivity;
import com.apkfuns.jsbridgesample.view.fragment.CustomFragment;

import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;

/**
 * Created by pengwei on 2017/9/21.
 */

public class TabActivity extends BaseActivity implements OnTabItemSelectListener {

    private Fragment[] fragments = new Fragment[]{new CustomFragment(),
            CustomFragment.newInstance("http://www.baidu.com"), new CustomFragment()};
    private Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        int selectedColor = Color.parseColor("#eeeeee");
        PagerBottomTabLayout bottomTabLayout = (PagerBottomTabLayout) findViewById(R.id.tab);
        Controller tabController = bottomTabLayout.builder()
                .addTabItem(R.mipmap.ic_launcher, "小度", Color.WHITE)
                .addTabItem(R.mipmap.ic_launcher, "消息", Color.WHITE)
                .addTabItem(R.mipmap.ic_launcher, "我", Color.WHITE)
                .setDefaultColor(selectedColor)
                .build();
        tabController.addTabItemClickListener(this);
        tabController.setBackgroundColor(Color.parseColor("#3b73af"));
        setFragment(fragments[tabController.getSelected()]);
    }

    @Override
    public void onSelected(int index, Object tag) {
        setFragment(fragments[index]);
    }

    @Override
    public void onRepeatClick(int index, Object tag) {

    }

    /**
     * 切换默认视图
     *
     * @param fragment
     */
    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            if (!fragment.equals(currentFragment)) {
                if (!fragment.isAdded()) {
                    transaction.hide(currentFragment).add(R.id.container, fragment).commit();
                } else {
                    transaction.hide(currentFragment).show(fragment).commit();
                }
            }
        } else {
            transaction.add(R.id.container, fragment).commit();
        }
        currentFragment = fragment;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}
