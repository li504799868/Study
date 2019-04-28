package com.lzp.dagger2;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by li.zhipeng on 2019/4/9.
 *
 * 组件类
 * 其中@Component提供注入来源
 */
@ActivityScope
@Component(modules = CommonModule.class)
public interface CommonComponent {
    void inject(LoginActivity activity);
}
