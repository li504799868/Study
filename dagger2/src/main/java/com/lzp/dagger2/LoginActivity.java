package com.lzp.dagger2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import javax.inject.Inject;

/**
 * Created by li.zhipeng on 2019/4/9.
 */
public class LoginActivity extends AppCompatActivity implements ICommonView {

    @Inject
    LoginPresenter presenter;

    @Inject
    LoginPresenter presenter2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.login(new User());
            }
        });
        DaggerCommonComponent
                .builder()
                // 创建CommonModule
                .commonModule(new CommonModule(this))
                .build()
                .inject(this);


        Log.e("lzp", presenter.toString());
        Log.e("lzp", presenter2.toString());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
