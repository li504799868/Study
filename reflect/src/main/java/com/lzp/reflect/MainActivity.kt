package com.lzp.reflect

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IntDef
import android.support.annotation.IntegerRes
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.canonicalName

    companion object {


        private const val NONE = 0
        private const val FIRST = 1
        private const val SECOND = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //
        test(NONE)

    }

    private fun test(@Test position: Int) {

    }

    @IntDef(
            NONE,
            FIRST,
            SECOND
    )
    @Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
    //表示注解作用范围，参数注解，成员注解，方法注解
    @Retention(AnnotationRetention.SOURCE)
    annotation class Test




}
