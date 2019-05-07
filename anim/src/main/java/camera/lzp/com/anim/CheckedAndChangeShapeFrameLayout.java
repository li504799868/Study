package camera.lzp.com.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by li.zhipeng on 2017/7/11.
 * <p>
 * 改变形状和颜色的按钮, 关闭时为原型，打开为全圆角长方形
 */

public class CheckedAndChangeShapeFrameLayout extends LinearLayout implements Checkable {

    /**
     * 按钮的状态
     */
    public interface Status {
        // 普通状态
        int CLOSED = 0;
        // 成功状态
        int OPEN = 1;
    }

    /**
     * 关闭的位置
     */
    public interface CloseGravity {
        int START = 0;
        int END = 1;
        int CENTER = 2;
    }

    /**
     * 动画时长
     */
    private int mDuration = 500;

    /**
     * 目前的状态
     */
    private int mCurrentStatus = Status.OPEN;

    /**
     * 普通状态的背景色
     */
    private int mNormalBgColor;

    /**
     * 选中状态的颜色
     */
    private int mCheckedBgColor;

    /**
     * 要绘制的颜色，默认是未选中状态
     */
    private int mPaintColor = mNormalBgColor;

    /**
     * 是否正在动画中
     */
    private boolean isAnim;

    /**
     * 圆角的大小
     */
    private int mRadius;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 绘制的形状区域
     */
    private RectF mRectF;

    /**
     * 设置最大距离
     */
    private int maxPadding;

    /**
     * 当前的Padding
     */
    private int mCurrentPadding;

    /**
     * 最小大小
     */
    private int minSize = 20;

    /**
     * 关闭的位置
     */
    private int closeGravity;

    public CheckedAndChangeShapeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedAndChangeShapeFrameLayout);
        // 默认背景颜色
        mNormalBgColor = typedArray.getColor(R.styleable.CheckedAndChangeShapeFrameLayout_normalColor, Color.parseColor("#373744"));
        // 选中背景颜色
        mCheckedBgColor = typedArray.getColor(R.styleable.CheckedAndChangeShapeFrameLayout_checkedColor, Color.parseColor("#F0565E"));
        // 被选中的状态
        setChecked(typedArray.getBoolean(R.styleable.CheckedAndChangeShapeFrameLayout_android_checked, false));
        // 设置打开状态
        this.mCurrentStatus = typedArray.getBoolean(R.styleable.CheckedAndChangeShapeFrameLayout_open, false) ? Status.OPEN : Status.CLOSED;
        // 关闭的位置
        closeGravity = typedArray.getInt(R.styleable.CheckedAndChangeShapeFrameLayout_closeGravity, CloseGravity.START);
        post(() -> setStatusWithoutAnim(mCurrentStatus));
        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 计算最大间距
        maxPadding = width - minSize;
        // 最小是宽或高
        minSize = width > height ? height : width;
        mRadius = minSize;
    }

    @Override
    public void setChecked(boolean checked) {
        mPaintColor = checked ? mCheckedBgColor : mNormalBgColor;
        invalidate();
    }

    @Override
    public boolean isChecked() {
        return mPaintColor == mCheckedBgColor;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
        invalidate();
    }

    public boolean isOpen() {
        return mCurrentStatus == Status.OPEN;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRectF == null) {
            mRectF = new RectF();
        }
        // 判断宽高
        int width = getWidth();
        int height = getHeight();

        int left, right;
        // 靠左
        if (closeGravity == CloseGravity.START) {
            left = 0;
            right = width - mCurrentPadding < minSize ? minSize : width - mCurrentPadding;
        }
        // 靠右
        else if (closeGravity == CloseGravity.END) {
            left = width - mCurrentPadding < minSize ? width - minSize : mCurrentPadding;
            right = width;
        }
        // 居中
        else {
            left = width - mCurrentPadding * 2 < minSize ? (width - minSize) / 2 : mCurrentPadding;
            right = getWidth() - left;
        }

        mRectF.set(left, 0, right, height);
        // 开始画后面的背景
        mPaint.setColor(mPaintColor);
        canvas.clipRect(mRectF);
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
        // 这里是画文字的部分，我们不管
        super.onDraw(canvas);


    }

    /**
     * 设置按钮状态
     */
    public void setStatus(int status) {
        if (status != mCurrentStatus && !isAnim) {
            switch (status) {
                case Status.CLOSED:
                    startAnimSet(mPaintColor, mPaintColor, mRadius, mRadius, 0, maxPadding);
                    break;
                case Status.OPEN:
                    startAnimSet(mPaintColor, mPaintColor, mRadius, mRadius, maxPadding, 0);
                    break;
            }
            mCurrentStatus = status;
        }
    }

    /**
     * 设置按钮状态
     */
    public void setStatusWithoutAnim(int status) {
        switch (status) {
            case Status.CLOSED:
                mCurrentPadding = maxPadding;
                break;
            case Status.OPEN:
                mCurrentPadding = 0;
                break;
        }
        mCurrentStatus = status;
        invalidate();
    }

    /**
     * 获取当前状态
     */
    public int getStatus() {
        return mCurrentStatus;
    }

    /**
     * 开启动画效果
     */
    private void startAnimSet(int fromColor, int toColor, int fromRadius, int roRadius, int fromPadding, int toPadding) {
        isAnim = true;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(getColorAnim(fromColor, toColor), getRadiusAnim(fromRadius, roRadius), getShapeAnim(fromPadding, toPadding));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    /**
     * 颜色动画
     */
    private ValueAnimator getColorAnim(int fromColor, int toColor) {
        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnim.setDuration(mDuration);
        colorAnim.addUpdateListener(valueAnimator -> {
            mPaintColor = (int) valueAnimator.getAnimatedValue();
            invalidate();
        });
        return colorAnim;
    }

    /**
     * 圆角动画
     */
    private ValueAnimator getRadiusAnim(int fromRadius, int toRadius) {
        {
            ValueAnimator radiusAnim = ValueAnimator.ofInt(fromRadius, toRadius);
            radiusAnim.setDuration(mDuration);
            radiusAnim.addUpdateListener(valueAnimator -> mRadius = (int) valueAnimator.getAnimatedValue());
            return radiusAnim;
        }
    }

    /**
     * 形状动画
     */
    private ValueAnimator getShapeAnim(int fromPadding, int toPadding) {
        {
            ValueAnimator shapeAnim = ValueAnimator.ofInt(fromPadding, toPadding);
            shapeAnim.setDuration(mDuration);
            shapeAnim.addUpdateListener(valueAnimator -> mCurrentPadding = (int) valueAnimator.getAnimatedValue());
            return shapeAnim;
        }
    }
}
