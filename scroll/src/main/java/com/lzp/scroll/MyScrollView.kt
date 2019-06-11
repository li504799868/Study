package com.lzp.scroll

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import android.widget.ScrollView

/**
 * Created by li.zhipeng on 2019-05-29.
 *
 *      自定义ScrollView
 */
class MyScrollView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    override fun postInvalidateOnAnimation() {
        super.postInvalidateOnAnimation()
    }

    override fun invalidate() {
        super.invalidate()
    }

    override fun onDescendantInvalidated(child: View, target: View) {
        super.onDescendantInvalidated(child, target)
    }

}