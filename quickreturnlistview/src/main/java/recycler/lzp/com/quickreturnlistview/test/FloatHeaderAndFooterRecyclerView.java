package recycler.lzp.com.quickreturnlistview.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by li.zhipeng on 2018/10/26.
 * <p>
 * 可以悬浮Header和Footer的RecyclerView
 */
public class FloatHeaderAndFooterRecyclerView extends RecyclerView {

    public static final int DURATION = 300;

    public static final int MIN_DISTANCE = 20;

    private View floatHeader;

    /**
     * 悬浮header占位
     */
    private View headerPlaceHolder;

    private BaseRecycleViewAdapter adapter;

    private boolean headerPlaceHolderAttach;

    private ObjectAnimator showFloatHeaderAnimator;

    private ObjectAnimator hideFloatHeaderAnimator;

    private ObjectAnimator scrollFloatHeaderAnimator;

    /**
     * 是否floatView已经显示在屏幕中
     */
    private boolean floatViewShow;

    private OnFloatViewStateChangedListener listener;

    public FloatHeaderAndFooterRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public FloatHeaderAndFooterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatHeaderAndFooterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        headerPlaceHolder = new View(context);
        initRecyclerView(context);
    }

    private void initRecyclerView(Context context) {
        setLayoutManager(new LinearLayoutManager(context));
        addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                if (view == headerPlaceHolder) {
                    headerPlaceHolderAttach = true;
                    if (floatHeader != null) {
                        floatHeader.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (view == headerPlaceHolder) {
                    headerPlaceHolderAttach = false;
                    if (floatHeader != null && !isHideFloatHeaderAnimRunning()) {
                        floatHeader.setVisibility(View.GONE);
                        if (listener != null){
                            listener.onFloatViewHide();
                        }
                    }
                }
            }
        });
    }

    public void setFloatHeader(@LayoutRes int layoutId) {
        setFloatHeader(LayoutInflater.from(getContext()).inflate(layoutId, this, false));
    }

    public void setFloatHeader(final View floatHeader) {
        this.floatHeader = floatHeader;
        if (this.floatHeader.getHeight() == 0) {
            this.floatHeader.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            initHeaderPlaceHolder();
                            floatHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });
        } else {
            initHeaderPlaceHolder();
        }
    }

    private void initHeaderPlaceHolder() {
        // 设置header占位的高度
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) headerPlaceHolder.getLayoutParams();
        if (params == null) {
            params = new RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT, floatHeader.getHeight());
        } else {
            params.height = floatHeader.getHeight();
        }
        headerPlaceHolder.setLayoutParams(params);
    }

    public void setAdapter(BaseRecycleViewAdapter adapter) {
        if (this.adapter == null || this.adapter != adapter) {
            this.adapter = adapter;
            super.setAdapter(adapter);
            adapter.addHeaderView(headerPlaceHolder);
        }
    }

    /**
     * RecyclerView 滑动监听
     */
    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
//        Log.e("lzp", "dy:" + dy);
//        Log.e("lzp", "headerPlaceHolder:" + headerPlaceHolder.getTop());
        if (headerPlaceHolderAttach) {
            setFloatHeaderTranslateY(dy);
        } else {
            if (Math.abs(dy) < MIN_DISTANCE) {
                return;
            }
            // floatHeader正在显示，如果向下滑动
            if (dy > 0) {
                hideFloatHeader();
            }
            // floatHeader没有显示，如果向上滑动
            else if (dy < 0) {
                showFloatHeader();
            }
        }
    }

    /**
     * 设置floatHeader的translationY
     */
    private void setFloatHeaderTranslateY(int dy) {
        // 向下滑动
        if (dy < 0) {
            if (headerPlaceHolder.getTop() > floatHeader.getTranslationY()) {
                cancelShowFloatHeaderAnim();
                floatHeader.setTranslationY(headerPlaceHolder.getTop());
            }
        }
        // 向下滑动
        else {
            // 如果floatHeader正在显示中，启动一个动画滑动到指定的位置
            if (floatHeader.getTranslationY() == 0) {
                scrollFloatHeader(headerPlaceHolder.getTop());
            } else {
                floatHeader.setTranslationY(headerPlaceHolder.getTop());
            }
        }

        if (listener != null) {
            listener.onFloatViewScroll();
        }
    }

    private synchronized void showFloatHeader() {
        if (floatHeader == null) {
            return;
        }
        if (isShowFloatHeaderAnimRunning()) {
            return;
        }
        if (floatHeader.getVisibility() == VISIBLE || floatViewShow) {
            floatViewShow = true;
            return;
        }
        this.floatViewShow = true;
        // 取消隐藏动画
        cancelHideFloatHeaderAnim();
        if (showFloatHeaderAnimator == null) {
            showFloatHeaderAnimator = ObjectAnimator.ofFloat(floatHeader, "translationY", floatHeader.getTranslationY(), 0);
            showFloatHeaderAnimator.setDuration(DURATION);
            showFloatHeaderAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationEnd(animation);
                    floatHeader.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationStart(Animator animation, boolean isReverse) {
                    floatHeader.setVisibility(View.VISIBLE);
                }
            });
        }
        showFloatHeaderAnimator.start();
        if (listener != null) {
            listener.onFloatViewShow();
        }
    }

    /**
     * 判断显示floatHeader动画是否正在显示
     */
    private boolean isShowFloatHeaderAnimRunning() {
        return showFloatHeaderAnimator != null && showFloatHeaderAnimator.isRunning();
    }

    private void cancelShowFloatHeaderAnim() {
        if (showFloatHeaderAnimator != null) {
            showFloatHeaderAnimator.cancel();
        }
    }

    private synchronized void hideFloatHeader() {
        if (floatHeader == null) {
            return;
        }
        if (isHideFloatHeaderAnimRunning()) {
            return;
        }
        if (floatHeader.getVisibility() == GONE || !floatViewShow) {
            floatViewShow = false;
            return;
        }
        this.floatViewShow = false;
        // 取消显示动画
        cancelShowFloatHeaderAnim();
        if (hideFloatHeaderAnimator == null) {
            hideFloatHeaderAnimator = ObjectAnimator.ofFloat(floatHeader, "translationY",
                    floatHeader.getTranslationY(), -floatHeader.getHeight());
            hideFloatHeaderAnimator.setDuration(DURATION);
            hideFloatHeaderAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    floatHeader.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    floatHeader.setVisibility(View.GONE);
                }
            });
        }
        hideFloatHeaderAnimator.start();
        if (listener != null) {
            listener.onFloatViewHide();
        }
    }

    /**
     * 判断隐藏floatHeader动画是否正在显示
     */
    private boolean isHideFloatHeaderAnimRunning() {
        return hideFloatHeaderAnimator != null && hideFloatHeaderAnimator.isRunning();
    }

    private void cancelHideFloatHeaderAnim() {
        if (hideFloatHeaderAnimator != null) {
            hideFloatHeaderAnimator.end();
        }
    }

    /**
     * 滚动floatHeader到指定的位置
     */
    private synchronized void scrollFloatHeader(float translationY) {
        if (floatHeader == null) {
            return;
        }
        if (scrollFloatHeaderAnimator != null && scrollFloatHeaderAnimator.isRunning()) {
            return;
        }
        if (floatHeader.getVisibility() == GONE) {
            return;
        }
        cancelShowFloatHeaderAnim();
        if (scrollFloatHeaderAnimator == null) {
            scrollFloatHeaderAnimator = ObjectAnimator.ofFloat(floatHeader, "translationY",
                    floatHeader.getTranslationY(), translationY);
        } else {
            scrollFloatHeaderAnimator.setFloatValues(floatHeader.getTranslationY(), translationY);
        }
        // 按照滑动距离的比例，计算动画的时间
        scrollFloatHeaderAnimator.setDuration((long) (Math.abs(translationY - floatHeader.getTranslationY()) / floatHeader.getHeight() * DURATION));
        scrollFloatHeaderAnimator.start();
    }

    public void setListener(OnFloatViewStateChangedListener listener) {
        this.listener = listener;
    }

    public interface OnFloatViewStateChangedListener {

        void onFloatViewShow();

        void onFloatViewHide();

        void onFloatViewScroll();

    }


}
