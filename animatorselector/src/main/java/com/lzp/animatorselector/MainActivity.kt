package com.lzp.animatorselector

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val STATE_CHECKED = intArrayOf(android.R.attr.state_checked)
    private val STATE_UNCHECKED = intArrayOf()
    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image.setOnClickListener {
            if (flag) {
                image.setImageState(STATE_UNCHECKED, true)
                flag = false
            } else {
                image.setImageState(STATE_CHECKED, true)
                flag = true
            }
        }
    }
}
