package com.lzp.rxjavastudy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Single.just("1")
                .map {
                    "2"
                }
                .map {
                    "3"
                }
                .subscribe({
                    toast("success")
                }, {
                    toast("failed")
                })
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
