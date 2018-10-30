package recycler.lzp.com.quickreturnlistview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by li.zhipeng on 2018/10/29.
 */
public class FloatHeaderAndFooterListView extends FrameLayout {

    /**
     * 浮动的View
     */
    private View floatView;

    private ListView listView;

    private View mPlaceHolder;

    private int mCachedVerticalScrollRange;
    private int mQuickReturnHeight;

    private int mItemOffsetY[];
    private boolean scrollIsComputed = false;
    private int mHeight;

    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private static final int STATE_EXPANDED = 3;
    private int mState = STATE_ONSCREEN;
    private int mScrollY;
    private int mMinRawY = 0;
    private int rawY;
    private boolean noAnimation = false;

    private TranslateAnimation anim;

    public FloatHeaderAndFooterListView(Context context) {
        this(context, null);
    }

    public FloatHeaderAndFooterListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatHeaderAndFooterListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        listView = new ListView(context);
        addView(listView);
        mPlaceHolder = new View(context);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (floatView == null){
                    return;
                }
                mScrollY = 0;
                int translationY = 0;

                if (scrollYIsComputed()) {
                    mScrollY = getComputedScrollY();
                }

                rawY = mPlaceHolder.getTop()
                        - Math.min(mCachedVerticalScrollRange - getHeight(), mScrollY);

                switch (mState) {
                    case STATE_OFFSCREEN:
                        if (rawY <= mMinRawY) {
                            mMinRawY = rawY;
                        } else {
                            mState = STATE_RETURNING;
                        }
                        translationY = rawY;
                        break;

                    case STATE_ONSCREEN:
                        if (rawY < -mQuickReturnHeight) {
                            System.out.println("test3");
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                        translationY = rawY;
                        break;

                    case STATE_RETURNING:

                        if (rawY > 0) {
                            mState = STATE_ONSCREEN;
                            translationY = rawY;
                        } else if (translationY < -mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;

                        } else if (floatView.getTranslationY() != 0
                                && !noAnimation) {
                            noAnimation = true;
                            anim = new TranslateAnimation(0, 0,
                                    -mQuickReturnHeight, 0);
                            anim.setFillAfter(true);
                            anim.setDuration(250);
                            floatView.startAnimation(anim);
                            anim.setAnimationListener(new Animation.AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                    // TODO Auto-generated method stub

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    noAnimation = false;
                                    mMinRawY = rawY;
                                    mState = STATE_EXPANDED;
                                }
                            });
                        }
                        break;

                    case STATE_EXPANDED:
                        if (rawY < mMinRawY - 2 && !noAnimation) {
                            noAnimation = true;
                            anim = new TranslateAnimation(0, 0, 0,
                                    -mQuickReturnHeight);
                            anim.setFillAfter(true);
                            anim.setDuration(250);
                            anim.setAnimationListener(new Animation.AnimationListener() {

                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    noAnimation = false;
                                    mState = STATE_OFFSCREEN;
                                }
                            });
                            floatView.startAnimation(anim);
                        } else if (rawY > 0) {
                            mState = STATE_ONSCREEN;
                            translationY = rawY;
                        } else if (translationY < -mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        } else {
                            mMinRawY = rawY;
                        }
                }
                floatView.setTranslationY(translationY);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }

    public void computeScrollY() {
        mHeight = 0;
        int mItemCount = listView.getAdapter().getCount();
        if (mItemOffsetY == null) {
            mItemOffsetY = new int[mItemCount];
        }
        for (int i = 0; i < mItemCount; ++i) {
            View view = listView.getAdapter().getView(i, null, this);
            view.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mItemOffsetY[i] = mHeight;
            mHeight += view.getMeasuredHeight();
            System.out.println(mHeight);
        }
        scrollIsComputed = true;
    }

    public boolean scrollYIsComputed() {
        return scrollIsComputed;
    }

    public int getListHeight() {
        return mHeight;
    }

    public int getComputedScrollY() {
        int pos, nScrollY, nItemY;
        View view = getChildAt(0);
        pos = listView.getFirstVisiblePosition();
        nItemY = view.getTop();
        nScrollY = mItemOffsetY[pos] - nItemY;
        return nScrollY;
    }

    public void setFloatView(final View floatView) {
        this.floatView = floatView;
        this.floatView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                AbsListView.LayoutParams params = (AbsListView.LayoutParams) mPlaceHolder.getLayoutParams();
                params.height = floatView.getHeight();
                mPlaceHolder.setLayoutParams(params);
//                floatView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        addView(this.floatView);
    }

    public void setAdapter(BaseAdapter adapter) {
        listView.setAdapter(adapter);
        listView.addHeaderView(mPlaceHolder);
        listView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mQuickReturnHeight = floatView.getHeight();
                        computeScrollY();
                        mCachedVerticalScrollRange = getListHeight();
                    }
                });
    }
}
