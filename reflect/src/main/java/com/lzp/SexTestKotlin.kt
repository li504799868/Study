package com.lzp

import android.support.annotation.IntDef

import java.lang.annotation.RetentionPolicy
import kotlin.annotation.Retention
import kotlin.annotation.MustBeDocumented

/**
 * Created by li.zhipeng on 2019/3/28.
 */
object SexTestKotlin {

    const val MAN = 2
    const val WOMEN = 3

    /**
     * 只能使用 [.MAN] [.WOMEN]
     */
    @MustBeDocumented // 表示开启Doc文档
    @IntDef(MAN, WOMEN) //限定为MAN,WOMEN
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER) //表示注解作用范围，参数注解，成员注解，方法注解
    @Retention(AnnotationRetention.SOURCE) //表示注解所存活的时间,在运行时,而不会存在 .class 文件中
    annotation class Sex//接口，定义新的注解类型

    @JvmStatic
    fun main(args: Array<String>) {
        setSex(1)
    }

    private fun setSex(@Sex man: Int) {}
}
