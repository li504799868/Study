package com.lzp.pagersnaphelper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.PagerSnapHelper
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        LinearSnapHelper().attachToRecyclerView(recyclerview)
        PagerSnapHelper().attachToRecyclerView(recyclerview).
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = SimpleTestAdapter()

    }
}
