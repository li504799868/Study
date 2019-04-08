package com.lzp;

import android.support.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by li.zhipeng on 2019/3/28.
 */
public class SexTest {

    public static final int MAN = 2;
    public static final int WOMEN = 3;

    /**
     * 只能使用 {@link #MAN} {@link #WOMEN}
     */
    @Documented // 表示开启Doc文档
    @IntDef({
            MAN,
            WOMEN
    }) //限定为MAN,WOMEN
    @Target({
            ElementType.PARAMETER,
            ElementType.FIELD,
            ElementType.METHOD,
    }) //表示注解作用范围，参数注解，成员注解，方法注解
    @Retention(RetentionPolicy.SOURCE) //表示注解所存活的时间,在运行时,而不会存在 .class 文件中
    public @interface Sex { //接口，定义新的注解类型
    }

    public static void main(String[] args){
        setSex(1);
    }

    private static void setSex(@Sex int man) {
    }
}
