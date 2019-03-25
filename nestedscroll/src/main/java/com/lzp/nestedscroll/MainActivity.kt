package com.lzp.nestedscroll

import android.app.ActivityManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.opengl.ETC1.getHeight
import android.support.annotation.Nullable
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    lateinit var rv: RecyclerView
    lateinit var nsv: MyNestedScrollView

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv = findViewById(R.id.rv)
        nsv = findViewById(R.id.nsv)


        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = SimpleTestAdapter()


        val rootView = findViewById<View>(android.R.id.content)

        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val topView1 = findViewById<View>(R.id.top_1)
                val topView2 = findViewById<View>(R.id.top_2)

                nsv.setMyScrollHeight(topView1.height)
                val rvNewHeight = rootView.height - topView2.height

                rv.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rvNewHeight)

            }
        })


    }
}
