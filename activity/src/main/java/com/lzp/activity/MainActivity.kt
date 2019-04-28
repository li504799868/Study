package com.lzp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        findViewById<View>(R.id.textview).setOnClickListener {
            val intent = Intent(this@MainActivity, TransparentActivity::class.java)
            startActivity(intent)
        }
    }
}
