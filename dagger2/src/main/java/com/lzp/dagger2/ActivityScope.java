package com.lzp.dagger2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by li.zhipeng on 2019/4/9.
 *
 * 自定义注解，被注解的元素会绑定对应的Activity的生命周期
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {
}
