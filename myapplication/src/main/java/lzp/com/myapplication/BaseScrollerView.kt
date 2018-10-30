package lzp.com.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller


/**
 * Created by li.zhipeng on 2018/5/3.
 *
 *      图片滑动优化
 *
 *      1、优化Fling的效果
 *
 */
class BaseScrollerView(context: Context, attributes: AttributeSet?, defStyleAttr: Int)
    : View(context, attributes, defStyleAttr) {

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context) : this(context, null)

    /**
     * 最大宽度，大于等于width
     * */
    private var maxWidth: Int = 20000

    /**
     * 滚动器Scroller
     * */
    private val scroller: OverScroller = OverScroller(context)

    /**
     * 记录手指划过的距离
     * */
    private var offsetX: Float = 0f

    /**
     * 手势处理
     * */
    private val gestureDetector: GestureDetector = GestureDetector(context, ChartGesture())

    /**
     * 重写手势
     * */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * 检查滚动的范围是否已经越界
     *
     * @return 是否已经到了边界，如果已经到了边界，可以停止滚动
     * */
    private fun checkBounds(): Boolean {
        // 如果小于0，那么等于0
        if (offsetX < 0) {
            offsetX = 0f
            return true
        }
        // 如果已经大于了最右边界
        else if (offsetX > maxWidth - width) {
            offsetX = (maxWidth - width).toFloat()
            return true
        }
        return false
    }

    /**
     * 图表手势处理类
     * */
    private inner class ChartGesture : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            // 如果scroller正在滑动, 停止滑动
            if (!scroller.isFinished) {
                scroller.abortAnimation()
            }
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            // 计算移动的位置
            offsetX += distanceX
            // 边界检查
            checkBounds()
            invalidate()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            Log.e("lzp", "velocity is :$velocityX")
            scroller.fling(offsetX.toInt(), 0,
                    -velocityX.toInt(), velocityY.toInt(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE,
                    0, 0)
            invalidate()
            return true
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            offsetX = scroller.currX.toFloat()
            val isBound = checkBounds()
            Log.e("lzp", "offsetX is :$offsetX")
            invalidate()
            if (isBound) {
                scroller.abortAnimation()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}