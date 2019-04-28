package com.lzp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by li.zhipeng on 2019-04-27.
 */
public class BaseActivity extends AppCompatActivity {

    static {
        Log.e("BaseActivity", "static function");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(getLocalClassName(), "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(getLocalClassName(), "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(getLocalClassName(), "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(getLocalClassName(), "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(getLocalClassName(), "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(getLocalClassName(), "onDestroy");
    }
}
