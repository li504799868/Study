package com.lzp.dagger2

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.View

class MainActivity : AppCompatActivity(), ICommonView {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_login).setOnClickListener {
        }

    }

    override fun getContext(): Context {
        return this
    }
}
