package com.example.johnanna.study

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlin.math.log

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        startActivity(Intent(this, MainActivity::class.java))
//        val uri = intent.data
//        Log.e("lzp", uri.getQueryParameter("id"))
    }
}
