package com.lzp.turntable

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Created by li.zhipeng on 2018/6/22.
 */
object Utils {

    fun dip2px(context: Context, dipValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dipValue * scale + 0.5f
    }

    @Suppress("DEPRECATION")
    fun getDrawable(context: Context, @DrawableRes id: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(id)
        } else {
            context.resources.getDrawable(id)
        }
    }
}