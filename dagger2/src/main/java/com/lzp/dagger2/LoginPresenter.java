package com.lzp.dagger2;

import android.content.Context;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by li.zhipeng on 2019/4/9.
 */
//@Singleton
public class LoginPresenter {

    ICommonView iView;

    @Inject
    public LoginPresenter(ICommonView iView){

        this.iView = iView;
    }

    public void login(User user){

        Context mContext = iView.getContext();
        Toast.makeText(mContext,"login......", Toast.LENGTH_SHORT).show();
    }
}
