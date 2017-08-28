package com.apkfuns.jsbridgesample.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.apkfuns.jsbridgesample.R;
import com.apkfuns.jsbridgesample.view.fragment.CustomFragment;

/**
 * Created by pengwei on 2017/8/14.
 */

public class CustomFragmentActivity extends AppCompatActivity {
    public static final String TAG = "CustomFragmentActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        getSupportFragmentManager().beginTransaction().add(R.id.content,
                new CustomFragment(), TAG).commit();
    }
}
