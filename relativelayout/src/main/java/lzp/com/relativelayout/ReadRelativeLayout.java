/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.widget;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

import android.annotation.NonNull;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Pools.SynchronizedPool;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews.RemoteView;

import com.android.internal.R;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A Layout where the positions of the children can be described in relation to each other or to the
 * parent.
 * <p>
 * <p>
 * Note that you cannot have a circular dependency between the size of the RelativeLayout and the
 * position of its children. For example, you cannot have a RelativeLayout whose height is set to
 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT WRAP_CONTENT} and a child set to
 * {@link #ALIGN_PARENT_BOTTOM}.
 * </p>
 * <p>
 * <p><strong>Note:</strong> In platform version 17 and lower, RelativeLayout was affected by
 * a measurement bug that could cause child views to be measured with incorrect
 * {@link android.view.View.MeasureSpec MeasureSpec} values. (See
 * {@link android.view.View.MeasureSpec#makeMeasureSpec(int, int) MeasureSpec.makeMeasureSpec}
 * for more details.) This was triggered when a RelativeLayout container was placed in
 * a scrolling container, such as a ScrollView or HorizontalScrollView. If a custom view
 * not equipped to properly measure with the MeasureSpec mode
 * {@link android.view.View.MeasureSpec#UNSPECIFIED UNSPECIFIED} was placed in a RelativeLayout,
 * this would silently work anyway as RelativeLayout would pass a very large
 * {@link android.view.View.MeasureSpec#AT_MOST AT_MOST} MeasureSpec instead.</p>
 * <p>
 * <p>This behavior has been preserved for apps that set <code>android:targetSdkVersion="17"</code>
 * or older in their manifest's <code>uses-sdk</code> tag for compatibility. Apps targeting SDK
 * version 18 or newer will receive the correct behavior</p>
 * <p>
 * <p>See the <a href="{@docRoot}guide/topics/ui/layout/relative.html">Relative
 * Layout</a> guide.</p>
 * <p>
 * <p>
 * Also see {@link android.widget.RelativeLayout.LayoutParams RelativeLayout.LayoutParams} for
 * layout attributes
 * </p>
 *
 * @attr ref android.R.styleable#RelativeLayout_gravity
 * @attr ref android.R.styleable#RelativeLayout_ignoreGravity
 */
@RemoteView
public class RelativeLayout extends ViewGroup {
    public static final int TRUE = -1;

    /**
     * Rule that aligns a child's right edge with another child's left edge.
     */
    public static final int LEFT_OF = 0;
    /**
     * Rule that aligns a child's left edge with another child's right edge.
     */
    public static final int RIGHT_OF = 1;
    /**
     * Rule that aligns a child's bottom edge with another child's top edge.
     */
    public static final int ABOVE = 2;
    /**
     * Rule that aligns a child's top edge with another child's bottom edge.
     */
    public static final int BELOW = 3;

    /**
     * Rule that aligns a child's baseline with another child's baseline.
     */
    public static final int ALIGN_BASELINE = 4;
    /**
     * Rule that aligns a child's left edge with another child's left edge.
     */
    public static final int ALIGN_LEFT = 5;
    /**
     * Rule that aligns a child's top edge with another child's top edge.
     */
    public static final int ALIGN_TOP = 6;
    /**
     * Rule that aligns a child's right edge with another child's right edge.
     */
    public static final int ALIGN_RIGHT = 7;
    /**
     * Rule that aligns a child's bottom edge with another child's bottom edge.
     */
    public static final int ALIGN_BOTTOM = 8;

    /**
     * Rule that aligns the child's left edge with its RelativeLayout
     * parent's left edge.
     */
    public static final int ALIGN_PARENT_LEFT = 9;
    /**
     * Rule that aligns the child's top edge with its RelativeLayout
     * parent's top edge.
     */
    public static final int ALIGN_PARENT_TOP = 10;
    /**
     * Rule that aligns the child's right edge with its RelativeLayout
     * parent's right edge.
     */
    public static final int ALIGN_PARENT_RIGHT = 11;
    /**
     * Rule that aligns the child's bottom edge with its RelativeLayout
     * parent's bottom edge.
     */
    public static final int ALIGN_PARENT_BOTTOM = 12;

    /**
     * Rule that centers the child with respect to the bounds of its
     * RelativeLayout parent.
     */
    public static final int CENTER_IN_PARENT = 13;
    /**
     * Rule that centers the child horizontally with respect to the
     * bounds of its RelativeLayout parent.
     */
    public static final int CENTER_HORIZONTAL = 14;
    /**
     * Rule that centers the child vertically with respect to the
     * bounds of its RelativeLayout parent.
     */
    public static final int CENTER_VERTICAL = 15;
    /**
     * Rule that aligns a child's end edge with another child's start edge.
     */
    public static final int START_OF = 16;
    /**
     * Rule that aligns a child's start edge with another child's end edge.
     */
    public static final int END_OF = 17;
    /**
     * Rule that aligns a child's start edge with another child's start edge.
     */
    public static final int ALIGN_START = 18;
    /**
     * Rule that aligns a child's end edge with another child's end edge.
     */
    public static final int ALIGN_END = 19;
    /**
     * Rule that aligns the child's start edge with its RelativeLayout
     * parent's start edge.
     */
    public static final int ALIGN_PARENT_START = 20;
    /**
     * Rule that aligns the child's end edge with its RelativeLayout
     * parent's end edge.
     */
    public static final int ALIGN_PARENT_END = 21;

    private static final int VERB_COUNT = 22;


    private static final int[] RULES_VERTICAL = {
            ABOVE, BELOW, ALIGN_BASELINE, ALIGN_TOP, ALIGN_BOTTOM
    };

    private static final int[] RULES_HORIZONTAL = {
            LEFT_OF, RIGHT_OF, ALIGN_LEFT, ALIGN_RIGHT, START_OF, END_OF, ALIGN_START, ALIGN_END
    };

    /**
     * Used to indicate left/right/top/bottom should be inferred from constraints
     */
    private static final int VALUE_NOT_SET = Integer.MIN_VALUE;

    private View mBaselineView = null;

    private int mGravity = Gravity.START | Gravity.TOP;
    private final Rect mContentBounds = new Rect();
    private final Rect mSelfBounds = new Rect();
    private int mIgnoreGravity;

    private SortedSet<View> mTopToBottomLeftToRightSet = null;

    private boolean mDirtyHierarchy;
    private View[] mSortedHorizontalChildren;
    private View[] mSortedVerticalChildren;
    private final DependencyGraph mGraph = new DependencyGraph();

    // Compatibility hack. Old versions of the platform had problems
    // with MeasureSpec value overflow and RelativeLayout was one source of them.
    // Some apps came to rely on them. :(
    private boolean mAllowBrokenMeasureSpecs = false;
    // Compatibility hack. Old versions of the platform would not take
    // margins and padding into account when generating the height measure spec
    // for children during the horizontal measure pass.
    private boolean mMeasureVerticalWithPaddingMargin = false;

    // A default width used for RTL measure pass
    /**
     * Value reduced so as not to interfere with View's measurement spec. flags. See:
     * {@link View#MEASURED_SIZE_MASK}.
     * {@link View#MEASURED_STATE_TOO_SMALL}.
     **/
    private static final int DEFAULT_WIDTH = 0x00010000;

    public RelativeLayout(Context context) {
        this(context, null);
    }

    public RelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
        queryCompatibilityModes(context);
    }

    private void initFromAttributes(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RelativeLayout, defStyleAttr, defStyleRes);
        mIgnoreGravity = a.getResourceId(R.styleable.RelativeLayout_ignoreGravity, View.NO_ID);
        mGravity = a.getInt(R.styleable.RelativeLayout_gravity, mGravity);
        a.recycle();
    }

    private void queryCompatibilityModes(Context context) {
        int version = context.getApplicationInfo().targetSdkVersion;
        mAllowBrokenMeasureSpecs = version <= Build.VERSION_CODES.JELLY_BEAN_MR1;
        mMeasureVerticalWithPaddingMargin = version >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Defines which View is ignored when the gravity is applied. This setting has no
     * effect if the gravity is <code>Gravity.START | Gravity.TOP</code>.
     *
     * @param viewId The id of the View to be ignored by gravity, or 0 if no View
     *               should be ignored.
     * @attr ref android.R.styleable#RelativeLayout_ignoreGravity
     * @see #setGravity(int)
     */
    @android.view.RemotableViewMethod
    public void setIgnoreGravity(int viewId) {
        mIgnoreGravity = viewId;
    }

    /**
     * Describes how the child views are positioned.
     *
     * @return the gravity.
     * @attr ref android.R.styleable#RelativeLayout_gravity
     * @see #setGravity(int)
     * @see android.view.Gravity
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * Describes how the child views are positioned. Defaults to
     * <code>Gravity.START | Gravity.TOP</code>.
     * <p>
     * <p>Note that since RelativeLayout considers the positioning of each child
     * relative to one another to be significant, setting gravity will affect
     * the positioning of all children as a single unit within the parent.
     * This happens after children have been relatively positioned.</p>
     *
     * @param gravity See {@link android.view.Gravity}
     * @attr ref android.R.styleable#RelativeLayout_gravity
     * @see #setHorizontalGravity(int)
     * @see #setVerticalGravity(int)
     */
    @android.view.RemotableViewMethod
    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                gravity |= Gravity.START;
            }

            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
                gravity |= Gravity.TOP;
            }

            mGravity = gravity;
            requestLayout();
        }
    }

    @android.view.RemotableViewMethod
    public void setHorizontalGravity(int horizontalGravity) {
        final int gravity = horizontalGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if ((mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) != gravity) {
            mGravity = (mGravity & ~Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) | gravity;
            requestLayout();
        }
    }

    @android.view.RemotableViewMethod
    public void setVerticalGravity(int verticalGravity) {
        final int gravity = verticalGravity & Gravity.VERTICAL_GRAVITY_MASK;
        if ((mGravity & Gravity.VERTICAL_GRAVITY_MASK) != gravity) {
            mGravity = (mGravity & ~Gravity.VERTICAL_GRAVITY_MASK) | gravity;
            requestLayout();
        }
    }

    @Override
    public int getBaseline() {
        return mBaselineView != null ? mBaselineView.getBaseline() : super.getBaseline();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        mDirtyHierarchy = true;
    }

    /**
     * 对child进行排序
     */
    private void sortChildren() {
        final int count = getChildCount();
        // 保存竖直方向上的child数组
        if (mSortedVerticalChildren == null || mSortedVerticalChildren.length != count) {
            mSortedVerticalChildren = new View[count];
        }
        // 保存水平方向上的child数组
        if (mSortedHorizontalChildren == null || mSortedHorizontalChildren.length != count) {
            mSortedHorizontalChildren = new View[count];
        }

        final DependencyGraph graph = mGraph;
        graph.clear();

        for (int i = 0; i < count; i++) {
            graph.add(getChildAt(i));
        }
        // 根据依赖关系进行排序
        graph.getSortedViews(mSortedVerticalChildren, RULES_VERTICAL);
        graph.getSortedViews(mSortedHorizontalChildren, RULES_HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 如果调用了requestLayout，会为true，会重新排序
        if (mDirtyHierarchy) {
            mDirtyHierarchy = false;
            // 对child进行排序
            // 因为child里面存在依赖关系，而我们摆放child的顺序可能和依赖关系不同
            // 例如， A依赖B，但是A在B的前面
            // 所以要按照依赖关系排序，才能正确的遍历所有的child
            sortChildren();
        }

        // 测量的宽度，不一定是最后的宽度
        int myWidth = -1;
        // 测量的高度，不一定是最后的高度
        int myHeight = -1;

        // 最后确定的宽度
        int width = 0;
        // 最后确定的高度
        int height = 0;
        // 得到测量模式和测量宽高
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 如果宽度是match_parent，wrap_content 或者固定值
        // 记录测量的宽度
        if (widthMode != MeasureSpec.UNSPECIFIED) {
            // 记录测量的宽度
            myWidth = widthSize;
        }
        // 如果高度是match_parent，wrap_content 或者固定值
        // 记录测量的宽度
        if (heightMode != MeasureSpec.UNSPECIFIED) {
            // 记录测量的高度
            myHeight = heightSize;
        }

        // 如果高度是match_parent 或者固定值
        // 记录要确定的宽度
        if (widthMode == MeasureSpec.EXACTLY) {
            width = myWidth;
        }

        // 如果高度是match_parent 或者固定值
        // 记录要确定的高度
        if (heightMode == MeasureSpec.EXACTLY) {
            height = myHeight;
        }
        // 要忽略的view
        View ignore = null;
        // 水平方向的gravity，mGravity默认是start和top
        int gravity = mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        // 是否有水平方向的gravity
        final boolean horizontalGravity = gravity != Gravity.START && gravity != 0;
        // 竖直方向上的gravity
        gravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
        // 是否有竖直方向上的gravity
        final boolean verticalGravity = gravity != Gravity.TOP && gravity != 0;

        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;

        // 是否child使用了CENTER_IN_PARENT，或者CENTER_HORIZONTAL或者ALIGN_PARENT_END
        boolean offsetHorizontalAxis = false;
        // 是否child使用了CENTER_IN_PARENT，或者CENTER_VERTICAL或者ALIGN_PARENT_BOTTOM
        boolean offsetVerticalAxis = false;

        // 找到要忽略的view，其实这个属性很少用到
        if ((horizontalGravity || verticalGravity) && mIgnoreGravity != View.NO_ID) {
            ignore = findViewById(mIgnoreGravity);
        }
        // 判断宽高是否是wrap_content或者不受限
        final boolean isWrapContentWidth = widthMode != MeasureSpec.EXACTLY;
        final boolean isWrapContentHeight = heightMode != MeasureSpec.EXACTLY;

        // 得到布局方向
        final int layoutDirection = getLayoutDirection();
        // 如果是从右到左，且 宽度不受限，设置默认宽度
        if (isLayoutRtl() && myWidth == -1) {
            myWidth = DEFAULT_WIDTH;
        }
        // 开始测量水平方向上的child
        View[] views = mSortedHorizontalChildren;
        int count = views.length;
        // 开始第一次测量
        for (int i = 0; i < count; i++) {
            View child = views[i];
            if (child.getVisibility() != GONE) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                int[] rules = params.getRules(layoutDirection);
                // 根据依赖关系，计算child的left和right
                applyHorizontalSizeRules(params, myWidth, rules);
                // 测量child的宽度
                measureChildHorizontal(child, params, myWidth, myHeight);
                // 如果设置了CENTER_IN_PARENT， CENTER_HORIZONTAL 或者 ALIGN_PARENT_END
                // offsetHorizontalAxis 等于true
                if (positionChildHorizontal(child, params, myWidth, isWrapContentWidth)) {
                    offsetHorizontalAxis = true;
                }
            }
        }

        // 开始测量竖直方向的child
        views = mSortedVerticalChildren;
        count = views.length;
        final int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;

        for (int i = 0; i < count; i++) {
            final View child = views[i];
            if (child.getVisibility() != GONE) {
                final LayoutParams params = (LayoutParams) child.getLayoutParams();
                // 根据竖直方向上的依赖关系，确定child的top和bottom
                applyVerticalSizeRules(params, myHeight, child.getBaseline());
                // 同之前的测量一样，确定child的left，right，top，bottom，然后进行测量
                measureChild(child, params, myWidth, myHeight);
                // 如果设置了CENTER_IN_PARENT， CENTER_VERTICAL，ALIGN_PARENT_BOTTOM
                // offsetVerticalAxis = true
                if (positionChildVertical(child, params, myHeight, isWrapContentHeight)) {
                    offsetVerticalAxis = true;
                }
                // 如果parent的宽度是wrap_content或者不受限
                if (isWrapContentWidth) {
                    // 如果是从又想做
                    if (isLayoutRtl()) {
                        // 这里做了兼容处理
                        // 如果是4.4以下
                        if (targetSdkVersion < Build.VERSION_CODES.KITKAT) {
                            // 宽度 = 自身的宽度减去左边距
                            width = Math.max(width, myWidth - params.mLeft);
                        }
                        // 如果是4.4及以上版本
                        else {
                            // 宽度 = 自身的宽度减去左边距 - paddingLeft
                            width = Math.max(width, myWidth - params.mLeft + params.leftMargin);
                        }
                    } else {
                        // 如果是4.4以下
                        if (targetSdkVersion < Build.VERSION_CODES.KITKAT) {
                            // 宽度等于child的right
                            width = Math.max(width, params.mRight);
                        } else {
                            // 宽度等于child的right + paddingRight
                            width = Math.max(width, params.mRight + params.rightMargin);
                        }
                    }
                }

                // 如果parent的高度是wrap_content
                if (isWrapContentHeight) {
                    // 4.4以下，计算parent的高度 等于 child的bottom
                    if (targetSdkVersion < Build.VERSION_CODES.KITKAT) {
                        height = Math.max(height, params.mBottom);
                    } else {
                        // 4.4以下，计算parent的高度 等于 child的bottom + child的bottomMargin
                        height = Math.max(height, params.mBottom + params.bottomMargin);
                    }
                }

                if (child != ignore || verticalGravity) {
                    // 得到最小的left和top
                    left = Math.min(left, params.mLeft - params.leftMargin);
                    top = Math.min(top, params.mTop - params.topMargin);
                }

                if (child != ignore || horizontalGravity) {
                    // 得到最小的right和bottom
                    right = Math.max(right, params.mRight + params.rightMargin);
                    bottom = Math.max(bottom, params.mBottom + params.bottomMargin);
                }
            }
        }

        View baselineView = null;
        LayoutParams baselineParams = null;
        // 遍历竖直方向的view，找到mBaselineView
        for (int i = 0; i < count; i++) {
            final View child = views[i];
            // 如果child可见
            if (child.getVisibility() != GONE) {
                final LayoutParams childParams = (LayoutParams) child.getLayoutParams();
                // 找到最左边，最上面的的view作为mBaselineView
                if (baselineView == null || baselineParams == null
                        || compareLayoutPosition(childParams, baselineParams) < 0) {
                    baselineView = child;
                    baselineParams = childParams;
                }
            }
        }
        mBaselineView = baselineView;
        // 如果是parent的宽度是wrap_content或者不受限
        if (isWrapContentWidth) {
            // Width already has left padding in it since it was calculated by looking at
            // the right of each child view
            // 宽度加上paddingRight
            width += mPaddingRight;
            // 用计算的宽度和设置的宽度作对比，得到最大的宽度
            if (mLayoutParams != null && mLayoutParams.width >= 0) {
                width = Math.max(width, mLayoutParams.width);
            }
            // 与推荐的最小宽度对比（背景的宽度和minWidth）
            width = Math.max(width, getSuggestedMinimumWidth());
            // 根据新的宽度，生成新的宽度测量信息
            width = resolveSize(width, widthMeasureSpec);

            // 如果有child使用了居中属性，要重新确定child的left和right，因为宽度发生了改变
            if (offsetHorizontalAxis) {
                for (int i = 0; i < count; i++) {
                    final View child = views[i];
                    if (child.getVisibility() != GONE) {
                        final LayoutParams params = (LayoutParams) child.getLayoutParams();
                        final int[] rules = params.getRules(layoutDirection);
                        if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_HORIZONTAL] != 0) {
                            centerHorizontal(child, params, width);
                        } else if (rules[ALIGN_PARENT_RIGHT] != 0) {
                            final int childWidth = child.getMeasuredWidth();
                            params.mLeft = width - mPaddingRight - childWidth;
                            params.mRight = params.mLeft + childWidth;
                        }
                    }
                }
            }
        }

        // 如果是parent的高度是wrap_content或者不受限
        if (isWrapContentHeight) {
            // Height already has top padding in it since it was calculated by looking at
            // the bottom of each child view
            // 高度增加paddingBottom
            height += mPaddingBottom;

            // 用计算的高度和设置的高度作对比，得到最大的高度
            if (mLayoutParams != null && mLayoutParams.height >= 0) {
                height = Math.max(height, mLayoutParams.height);
            }

            // 与推荐的最小高度对比（背景的高度和minHeight）
            height = Math.max(height, getSuggestedMinimumHeight());
            // 生成新的高度测量模式
            height = resolveSize(height, heightMeasureSpec);
            // 如果有child使用了居中属性，要重新确定child的top和bottom，因为高度发生了改变
            if (offsetVerticalAxis) {
                for (int i = 0; i < count; i++) {
                    final View child = views[i];
                    if (child.getVisibility() != GONE) {
                        final LayoutParams params = (LayoutParams) child.getLayoutParams();
                        final int[] rules = params.getRules(layoutDirection);
                        if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_VERTICAL] != 0) {
                            centerVertical(child, params, height);
                        } else if (rules[ALIGN_PARENT_BOTTOM] != 0) {
                            final int childHeight = child.getMeasuredHeight();
                            params.mTop = height - mPaddingBottom - childHeight;
                            params.mBottom = params.mTop + childHeight;
                        }
                    }
                }
            }
        }

        // 如果使用了gravity属性
        if (horizontalGravity || verticalGravity) {
            final Rect selfBounds = mSelfBounds;
            // 更新自己的rect信息，可以绘制child的区域
            selfBounds.set(mPaddingLeft, mPaddingTop, width - mPaddingRight,
                    height - mPaddingBottom);

            final Rect contentBounds = mContentBounds;
            // 根据设置的gravity，得到实际要绘制内容的区域
            Gravity.apply(mGravity, right - left, bottom - top, selfBounds, contentBounds,
                    layoutDirection);
            // 水平偏移距离等于内容的left - 最小的left
            final int horizontalOffset = contentBounds.left - left;
            // 竖直偏移距离等于内容的top - 最小的top
            final int verticalOffset = contentBounds.top - top;
            // 如果绘制内容的区域有变化，再次更新child的left,right,top,bottom
            if (horizontalOffset != 0 || verticalOffset != 0) {
                for (int i = 0; i < count; i++) {
                    final View child = views[i];
                    if (child.getVisibility() != GONE && child != ignore) {
                        final LayoutParams params = (LayoutParams) child.getLayoutParams();
                        if (horizontalGravity) {
                            params.mLeft += horizontalOffset;
                            params.mRight += horizontalOffset;
                        }
                        if (verticalGravity) {
                            params.mTop += verticalOffset;
                            params.mBottom += verticalOffset;
                        }
                    }
                }
            }
        }

        // 如果是从右到左
        if (isLayoutRtl()) {
            // 得到测量的宽度和计算的宽度的差值
            final int offsetWidth = myWidth - width;
            for (int i = 0; i < count; i++) {
                final View child = views[i];
                if (child.getVisibility() != GONE) {
                    final LayoutParams params = (LayoutParams) child.getLayoutParams();
                    // 为了让child全部靠右，更新child的left和right，
                    params.mLeft -= offsetWidth;
                    params.mRight -= offsetWidth;
                }
            }
        }

        setMeasuredDimension(width, height);
    }

    /**
     * @return a negative number if the top of {@code p1} is above the top of
     * {@code p2} or if they have identical top values and the left of
     * {@code p1} is to the left of {@code p2}, or a positive number
     * otherwise
     */
    private int compareLayoutPosition(LayoutParams p1, LayoutParams p2) {
        final int topDiff = p1.mTop - p2.mTop;
        if (topDiff != 0) {
            return topDiff;
        }
        return p1.mLeft - p2.mLeft;
    }

    /**
     * Measure a child. The child should have left, top, right and bottom information
     * stored in its LayoutParams. If any of these values is VALUE_NOT_SET it means
     * that the view can extend up to the corresponding edge.
     *
     * @param child    Child to measure
     * @param params   LayoutParams associated with child
     * @param myWidth  Width of the the RelativeLayout
     * @param myHeight Height of the RelativeLayout
     */
    private void measureChild(View child, LayoutParams params, int myWidth, int myHeight) {
        int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft,
                params.mRight, params.width,
                params.leftMargin, params.rightMargin,
                mPaddingLeft, mPaddingRight,
                myWidth);
        int childHeightMeasureSpec = getChildMeasureSpec(params.mTop,
                params.mBottom, params.height,
                params.topMargin, params.bottomMargin,
                mPaddingTop, mPaddingBottom,
                myHeight);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    /**
     * 测量水平方向上的child
     */
    private void measureChildHorizontal(
            View child, LayoutParams params, int myWidth, int myHeight) {
        // 获得child水平方向的测量信息
        final int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft, params.mRight,
                params.width, params.leftMargin, params.rightMargin, mPaddingLeft, mPaddingRight,
                myWidth);
        // child高度的测量信息
        final int childHeightMeasureSpec;
        // 这里做了兼容处理，当版本小于等于4.2时，mAllowBrokenMeasureSpecs等于true
        // 如果高度不受限，且版本大于等于4.2
        if (myHeight < 0 && !mAllowBrokenMeasureSpecs) {
            // 如果child的高度是确定的，设置child高度的测量模式
            if (params.height >= 0) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        params.height, MeasureSpec.EXACTLY);
            } else {
                // child的高度也是不受限的
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
        } else {
            final int maxHeight;
            // 如果版本大于等于4.3
            if (mMeasureVerticalWithPaddingMargin) {
                // 最大高度，parent的高度减去了padding和margin
                maxHeight = Math.max(0, myHeight - mPaddingTop - mPaddingBottom
                        - params.topMargin - params.bottomMargin);
            } else {
                // 版本小于4.3 没有减去padding和margin
                maxHeight = Math.max(0, myHeight);
            }

            final int heightMode;
            // 如果child的高是match_parent，设置chil的测量模式是MeasureSpec.EXACTLY
            if (params.height == LayoutParams.MATCH_PARENT) {
                heightMode = MeasureSpec.EXACTLY;
            } else {
                // 否则设置child的高度的测量模式的wrap_content
                heightMode = MeasureSpec.AT_MOST;
            }
            // 生成新的child的高度的测量大小和测量模式
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, heightMode);
        }
        // 测量child
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    /**
     * Get a measure spec that accounts for all of the constraints on this view.
     * This includes size constraints imposed by the RelativeLayout as well as
     * the View's desired dimension.
     *
     * @param childStart   The left or top field of the child's layout params
     * @param childEnd     The right or bottom field of the child's layout params
     * @param childSize    The child's desired size (the width or height field of
     *                     the child's layout params)
     * @param startMargin  The left or top margin
     * @param endMargin    The right or bottom margin
     * @param startPadding mPaddingLeft or mPaddingTop
     * @param endPadding   mPaddingRight or mPaddingBottom
     * @param mySize       The width or height of this view (the RelativeLayout)
     * @return MeasureSpec for the child
     */
    private int getChildMeasureSpec(int childStart, int childEnd,
                                    int childSize, int startMargin, int endMargin, int startPadding,
                                    int endPadding, int mySize) {

        int childSpecMode = 0;
        int childSpecSize = 0;

        final boolean isUnspecified = mySize < 0;
        // 如果宽度不受限，且 版本是4.2及以下
        if (isUnspecified && !mAllowBrokenMeasureSpecs) {
            // 如果已经知道了child的left和right
            if (childStart != VALUE_NOT_SET && childEnd != VALUE_NOT_SET) {
                // 计算得到child的宽度
                childSpecSize = Math.max(0, childEnd - childStart);
                // 设置测量模式
                childSpecMode = MeasureSpec.EXACTLY;
            }
            // 如果child的宽度是固定值，设置大小和测量模式
            else if (childSize >= 0) {
                childSpecSize = childSize;
                childSpecMode = MeasureSpec.EXACTLY;
            } else {
                // 设置child的大小不受限制
                childSpecSize = 0;
                childSpecMode = MeasureSpec.UNSPECIFIED;
            }
            // 返回新的宽度测量信息
            return MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
        }

        int tempStart = childStart;
        int tempEnd = childEnd;

        // 如果child的left 还没确定
        if (tempStart == VALUE_NOT_SET) {
            // 设置开始位置时paddingLeft+marginLeft
            tempStart = startPadding + startMargin;
        }
        // 如果child的高度还没确定
        if (tempEnd == VALUE_NOT_SET) {
            // 设置结束位置时parent的宽度 - paddingRight - marginRight
            tempEnd = mySize - endPadding - endMargin;
        }

        // 最大的宽度
        final int maxAvailable = tempEnd - tempStart;
        // 如果确定了child的left和right
        if (childStart != VALUE_NOT_SET && childEnd != VALUE_NOT_SET) {
            // 得到新的测量模式，
            // MeasureSpec.UNSPECIFIED 大小不受限，一般只有系统使用
            // MeasureSpec.EXACTLY 固定值或 match_parent
            childSpecMode = isUnspecified ? MeasureSpec.UNSPECIFIED : MeasureSpec.EXACTLY;
            childSpecSize = Math.max(0, maxAvailable);
        } else {
            // 如果设置了child的宽度是固定值
            if (childSize >= 0) {
                // 直接设置测量模式是MeasureSpec.EXACTLY
                childSpecMode = MeasureSpec.EXACTLY;
                // 如果刚刚计算出来最大可用的宽度
                if (maxAvailable >= 0) {
                    // 设置的宽度 和 可用的宽度 ，取最小值
                    childSpecSize = Math.min(maxAvailable, childSize);
                } else {
                    // 直接使用设置的宽度
                    childSpecSize = childSize;
                }
            }
            // 如果child的宽度是match_parent
            else if (childSize == LayoutParams.MATCH_PARENT) {
                // 如果parent是不受限的，这里替换child的测量模式变为不受限
                childSpecMode = isUnspecified ? MeasureSpec.UNSPECIFIED : MeasureSpec.EXACTLY;
                // 宽度可用的宽度
                childSpecSize = Math.max(0, maxAvailable);
            }
            // 如果child的宽度是wrap_content
            else if (childSize == LayoutParams.WRAP_CONTENT) {
                // 如果有可用的空间
                if (maxAvailable >= 0) {
                    // 测量模式是wrap_content
                    childSpecMode = MeasureSpec.AT_MOST;
                    // 宽度等parent的可用宽度
                    childSpecSize = maxAvailable;
                } else {
                    // 设置child的宽度不受限
                    childSpecMode = MeasureSpec.UNSPECIFIED;
                    childSpecSize = 0;
                }
            }
        }

        return MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
    }

    /**
     * 确定child在水平方向上的位置
     * */
    private boolean positionChildHorizontal(View child, LayoutParams params, int myWidth,
                                            boolean wrapContent) {

        final int layoutDirection = getLayoutDirection();
        int[] rules = params.getRules(layoutDirection);

        // 如果没有确定child的left，设置left等right减去宽度
        if (params.mLeft == VALUE_NOT_SET && params.mRight != VALUE_NOT_SET) {
            // Right is fixed, but left varies
            params.mLeft = params.mRight - child.getMeasuredWidth();
        }
        // 如果没有确定child的right，设置right等left + 宽度
        else if (params.mLeft != VALUE_NOT_SET && params.mRight == VALUE_NOT_SET) {
            // Left is fixed, but right varies
            params.mRight = params.mLeft + child.getMeasuredWidth();
        }
        // 如果left和right都没确定
        else if (params.mLeft == VALUE_NOT_SET && params.mRight == VALUE_NOT_SET) {
            // Both left and right vary
            // 判断是否是否使用CENTER_IN_PARENT，或者CENTER_HORIZONTAL
            if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_HORIZONTAL] != 0) {
                // 如果不是wrap_content，设置child的left和right居中
                if (!wrapContent) {
                    centerHorizontal(child, params, myWidth);
                } else {
                    // 直接根据measure的宽度设置left和right
                    positionAtEdge(child, params, myWidth);
                }
                return true;
            } else {
                // This is the default case. For RTL we start from the right and for LTR we start
                // from the left. This will give LEFT/TOP for LTR and RIGHT/TOP for RTL.
                // 直接根据measure的宽度设置left和right
                positionAtEdge(child, params, myWidth);
            }
        }
        // 是否设置了ALIGN_PARENT_END
        return rules[ALIGN_PARENT_END] != 0;
    }

    private void positionAtEdge(View child, LayoutParams params, int myWidth) {
        if (isLayoutRtl()) {
            params.mRight = myWidth - mPaddingRight - params.rightMargin;
            params.mLeft = params.mRight - child.getMeasuredWidth();
        } else {
            params.mLeft = mPaddingLeft + params.leftMargin;
            params.mRight = params.mLeft + child.getMeasuredWidth();
        }
    }

    private boolean positionChildVertical(View child, LayoutParams params, int myHeight,
                                          boolean wrapContent) {

        int[] rules = params.getRules();

        if (params.mTop == VALUE_NOT_SET && params.mBottom != VALUE_NOT_SET) {
            // Bottom is fixed, but top varies
            params.mTop = params.mBottom - child.getMeasuredHeight();
        } else if (params.mTop != VALUE_NOT_SET && params.mBottom == VALUE_NOT_SET) {
            // Top is fixed, but bottom varies
            params.mBottom = params.mTop + child.getMeasuredHeight();
        } else if (params.mTop == VALUE_NOT_SET && params.mBottom == VALUE_NOT_SET) {
            // Both top and bottom vary
            if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_VERTICAL] != 0) {
                if (!wrapContent) {
                    centerVertical(child, params, myHeight);
                } else {
                    params.mTop = mPaddingTop + params.topMargin;
                    params.mBottom = params.mTop + child.getMeasuredHeight();
                }
                return true;
            } else {
                params.mTop = mPaddingTop + params.topMargin;
                params.mBottom = params.mTop + child.getMeasuredHeight();
            }
        }
        return rules[ALIGN_PARENT_BOTTOM] != 0;
    }

    /**
     * 根据水平方向的规则，设置child的left和right
     */
    private void applyHorizontalSizeRules(LayoutParams childParams, int myWidth, int[] rules) {
        RelativeLayout.LayoutParams anchorParams;

        // 重置child的left和right
        childParams.mLeft = VALUE_NOT_SET;
        childParams.mRight = VALUE_NOT_SET;
        // 获得leftOf属性
        anchorParams = getRelatedViewParams(rules, LEFT_OF);
        // 如果设置leftOf
        if (anchorParams != null) {
            // 计算child的right
            // 依赖的child的left 减去 左边距 减去 自身的右边距
            childParams.mRight = anchorParams.mLeft - (anchorParams.leftMargin +
                    childParams.rightMargin);
        }
        // childParams.alignWithParent表示如果依赖的child隐藏了，自动以parent作为锚点
        else if (childParams.alignWithParent && rules[LEFT_OF] != 0) {
            if (myWidth >= 0) {
                // 设置child的right等于parent的宽度 - paddingRight - 右边距
                childParams.mRight = myWidth - mPaddingRight - childParams.rightMargin;
            }
        }
        // 同理通过RIGHT_OF计算child的left
        anchorParams = getRelatedViewParams(rules, RIGHT_OF);
        if (anchorParams != null) {
            childParams.mLeft = anchorParams.mRight + (anchorParams.rightMargin +
                    childParams.leftMargin);
        } else if (childParams.alignWithParent && rules[RIGHT_OF] != 0) {
            childParams.mLeft = mPaddingLeft + childParams.leftMargin;
        }

        // 同理通过ALIGN_LEFT计算child的left
        anchorParams = getRelatedViewParams(rules, ALIGN_LEFT);
        if (anchorParams != null) {
            childParams.mLeft = anchorParams.mLeft + childParams.leftMargin;
        } else if (childParams.alignWithParent && rules[ALIGN_LEFT] != 0) {
            childParams.mLeft = mPaddingLeft + childParams.leftMargin;
        }
        // 同理通过ALIGN_RIGHT计算child的left
        anchorParams = getRelatedViewParams(rules, ALIGN_RIGHT);
        if (anchorParams != null) {
            childParams.mRight = anchorParams.mRight - childParams.rightMargin;
        } else if (childParams.alignWithParent && rules[ALIGN_RIGHT] != 0) {
            if (myWidth >= 0) {
                childParams.mRight = myWidth - mPaddingRight - childParams.rightMargin;
            }
        }
        // 如果设置了ALIGN_PARENT_LEFT，计算child的left值
        if (0 != rules[ALIGN_PARENT_LEFT]) {
            childParams.mLeft = mPaddingLeft + childParams.leftMargin;
        }
        // 如果设置了ALIGN_PARENT_RIGHT，计算child的left值
        if (0 != rules[ALIGN_PARENT_RIGHT]) {
            if (myWidth >= 0) {
                childParams.mRight = myWidth - mPaddingRight - childParams.rightMargin;
            }
        }
    }

    /**
     * 根据child在竖直方向上的依赖关系，确定child的top和bottom
     * */
    private void applyVerticalSizeRules(LayoutParams childParams, int myHeight, int myBaseline) {
        final int[] rules = childParams.getRules();

        // Baseline alignment overrides any explicitly specified top or bottom.
        // 如果设置了ALIGN_BASELINE，child的top默认是锚点View的top+baseline的偏移值
        int baselineOffset = getRelatedViewBaselineOffset(rules);
        if (baselineOffset != -1) {
            // 如果自己有baseline，与得到的baseline相减
            if (myBaseline != -1) {
                baselineOffset -= myBaseline;
            }
            // 只能确定child的top
            childParams.mTop = baselineOffset;
            childParams.mBottom = VALUE_NOT_SET;
            return;
        }

        RelativeLayout.LayoutParams anchorParams;
        // 设置child的top都是未确定状态
        childParams.mTop = VALUE_NOT_SET;
        childParams.mBottom = VALUE_NOT_SET;

        // 获取ABOVE属性
        anchorParams = getRelatedViewParams(rules, ABOVE);
        // 如果存在锚点View
        if (anchorParams != null) {
            // 设置child的bottom = 锚点的top - 锚点的topMargin - child的bottomMargin
            childParams.mBottom = anchorParams.mTop - (anchorParams.topMargin +
                    childParams.bottomMargin);
        }
        // 如果设置了alignWithParent，且 设置了ABOVE 属性
        else if (childParams.alignWithParent && rules[ABOVE] != 0) {
            // 如果parent的高度是确定的，以parent的高度设置child的bottom
            if (myHeight >= 0) {
                childParams.mBottom = myHeight - mPaddingBottom - childParams.bottomMargin;
            }
        }

        // 同理获取BELOW属性，设置child的top
        anchorParams = getRelatedViewParams(rules, BELOW);
        if (anchorParams != null) {
            childParams.mTop = anchorParams.mBottom + (anchorParams.bottomMargin +
                    childParams.topMargin);
        } else if (childParams.alignWithParent && rules[BELOW] != 0) {
            childParams.mTop = mPaddingTop + childParams.topMargin;
        }

        // 同理获取ALIGN_TOP属性，设置child的top
        anchorParams = getRelatedViewParams(rules, ALIGN_TOP);
        if (anchorParams != null) {
            childParams.mTop = anchorParams.mTop + childParams.topMargin;
        } else if (childParams.alignWithParent && rules[ALIGN_TOP] != 0) {
            childParams.mTop = mPaddingTop + childParams.topMargin;
        }

        // 同理获取ALIGN_BOTTOM属性，设置child的bottom
        anchorParams = getRelatedViewParams(rules, ALIGN_BOTTOM);
        if (anchorParams != null) {
            childParams.mBottom = anchorParams.mBottom - childParams.bottomMargin;
        } else if (childParams.alignWithParent && rules[ALIGN_BOTTOM] != 0) {
            if (myHeight >= 0) {
                childParams.mBottom = myHeight - mPaddingBottom - childParams.bottomMargin;
            }
        }

        // 如果设置了ALIGN_PARENT_TOP，计算child的top
        if (0 != rules[ALIGN_PARENT_TOP]) {
            childParams.mTop = mPaddingTop + childParams.topMargin;
        }

        // 如果设置了ALIGN_PARENT_BOTTOM，计算child的bottom
        if (0 != rules[ALIGN_PARENT_BOTTOM]) {
            if (myHeight >= 0) {
                childParams.mBottom = myHeight - mPaddingBottom - childParams.bottomMargin;
            }
        }
    }

    private View getRelatedView(int[] rules, int relation) {
        int id = rules[relation];
        if (id != 0) {
            DependencyGraph.Node node = mGraph.mKeyNodes.get(id);
            if (node == null) return null;
            View v = node.view;

            // Find the first non-GONE view up the chain
            while (v.getVisibility() == View.GONE) {
                rules = ((LayoutParams) v.getLayoutParams()).getRules(v.getLayoutDirection());
                node = mGraph.mKeyNodes.get((rules[relation]));
                // ignore self dependency. for more info look in git commit: da3003
                if (node == null || v == node.view) return null;
                v = node.view;
            }

            return v;
        }

        return null;
    }

    private LayoutParams getRelatedViewParams(int[] rules, int relation) {
        View v = getRelatedView(rules, relation);
        if (v != null) {
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if (params instanceof LayoutParams) {
                return (LayoutParams) v.getLayoutParams();
            }
        }
        return null;
    }

    private int getRelatedViewBaselineOffset(int[] rules) {
        final View v = getRelatedView(rules, ALIGN_BASELINE);
        if (v != null) {
            final int baseline = v.getBaseline();
            if (baseline != -1) {
                final ViewGroup.LayoutParams params = v.getLayoutParams();
                if (params instanceof LayoutParams) {
                    final LayoutParams anchorParams = (LayoutParams) v.getLayoutParams();
                    return anchorParams.mTop + baseline;
                }
            }
        }
        return -1;
    }

    private static void centerHorizontal(View child, LayoutParams params, int myWidth) {
        int childWidth = child.getMeasuredWidth();
        int left = (myWidth - childWidth) / 2;

        params.mLeft = left;
        params.mRight = left + childWidth;
    }

    private static void centerVertical(View child, LayoutParams params, int myHeight) {
        int childHeight = child.getMeasuredHeight();
        int top = (myHeight - childHeight) / 2;

        params.mTop = top;
        params.mBottom = top + childHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                RelativeLayout.LayoutParams st =
                        (RelativeLayout.LayoutParams) child.getLayoutParams();
                child.layout(st.mLeft, st.mTop, st.mRight, st.mBottom);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new RelativeLayout.LayoutParams(getContext(), attrs);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and no spanning.
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof RelativeLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (sPreserveMarginParamsInLayoutParamConversion) {
            if (lp instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) lp);
            } else if (lp instanceof MarginLayoutParams) {
                return new LayoutParams((MarginLayoutParams) lp);
            }
        }
        return new LayoutParams(lp);
    }

    /**
     * @hide
     */
    @Override
    public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        if (mTopToBottomLeftToRightSet == null) {
            mTopToBottomLeftToRightSet = new TreeSet<View>(new TopToBottomLeftToRightComparator());
        }

        // sort children top-to-bottom and left-to-right
        for (int i = 0, count = getChildCount(); i < count; i++) {
            mTopToBottomLeftToRightSet.add(getChildAt(i));
        }

        for (View view : mTopToBottomLeftToRightSet) {
            if (view.getVisibility() == View.VISIBLE
                    && view.dispatchPopulateAccessibilityEvent(event)) {
                mTopToBottomLeftToRightSet.clear();
                return true;
            }
        }

        mTopToBottomLeftToRightSet.clear();
        return false;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return RelativeLayout.class.getName();
    }

    /**
     * Compares two views in left-to-right and top-to-bottom fashion.
     */
    private class TopToBottomLeftToRightComparator implements Comparator<View> {
        public int compare(View first, View second) {
            // top - bottom
            int topDifference = first.getTop() - second.getTop();
            if (topDifference != 0) {
                return topDifference;
            }
            // left - right
            int leftDifference = first.getLeft() - second.getLeft();
            if (leftDifference != 0) {
                return leftDifference;
            }
            // break tie by height
            int heightDiference = first.getHeight() - second.getHeight();
            if (heightDiference != 0) {
                return heightDiference;
            }
            // break tie by width
            int widthDiference = first.getWidth() - second.getWidth();
            if (widthDiference != 0) {
                return widthDiference;
            }
            return 0;
        }
    }

    /**
     * Specifies how a view is positioned within a {@link RelativeLayout}.
     * The relative layout containing the view uses the value of these layout parameters to
     * determine where to position the view on the screen.  If the view is not contained
     * within a relative layout, these attributes are ignored.
     * <p>
     * See the <a href=“https://developer.android.com/guide/topics/ui/layout/relative.html”>
     * Relative Layout</a> guide for example code demonstrating how to use relative layout’s
     * layout parameters in a layout XML.
     * <p>
     * To learn more about layout parameters and how they differ from typical view attributes,
     * see the <a href=“https://developer.android.com/guide/topics/ui/declaring-layout.html#attributes”>
     * Layouts guide</a>.
     *
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignWithParentIfMissing
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_toLeftOf
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_toRightOf
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_above
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_below
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignBaseline
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignLeft
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignTop
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignRight
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignBottom
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignParentLeft
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignParentTop
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignParentRight
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignParentBottom
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_centerInParent
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_centerHorizontal
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_centerVertical
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_toStartOf
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_toEndOf
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignStart
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignEnd
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignParentStart
     * @attr ref android.R.styleable#RelativeLayout_Layout_layout_alignParentEnd
     */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        @ViewDebug.ExportedProperty(category = "layout", resolveId = true, indexMapping = {
                @ViewDebug.IntToString(from = ABOVE, to = "above"),
                @ViewDebug.IntToString(from = ALIGN_BASELINE, to = "alignBaseline"),
                @ViewDebug.IntToString(from = ALIGN_BOTTOM, to = "alignBottom"),
                @ViewDebug.IntToString(from = ALIGN_LEFT, to = "alignLeft"),
                @ViewDebug.IntToString(from = ALIGN_PARENT_BOTTOM, to = "alignParentBottom"),
                @ViewDebug.IntToString(from = ALIGN_PARENT_LEFT, to = "alignParentLeft"),
                @ViewDebug.IntToString(from = ALIGN_PARENT_RIGHT, to = "alignParentRight"),
                @ViewDebug.IntToString(from = ALIGN_PARENT_TOP, to = "alignParentTop"),
                @ViewDebug.IntToString(from = ALIGN_RIGHT, to = "alignRight"),
                @ViewDebug.IntToString(from = ALIGN_TOP, to = "alignTop"),
                @ViewDebug.IntToString(from = BELOW, to = "below"),
                @ViewDebug.IntToString(from = CENTER_HORIZONTAL, to = "centerHorizontal"),
                @ViewDebug.IntToString(from = CENTER_IN_PARENT, to = "center"),
                @ViewDebug.IntToString(from = CENTER_VERTICAL, to = "centerVertical"),
                @ViewDebug.IntToString(from = LEFT_OF, to = "leftOf"),
                @ViewDebug.IntToString(from = RIGHT_OF, to = "rightOf"),
                @ViewDebug.IntToString(from = ALIGN_START, to = "alignStart"),
                @ViewDebug.IntToString(from = ALIGN_END, to = "alignEnd"),
                @ViewDebug.IntToString(from = ALIGN_PARENT_START, to = "alignParentStart"),
                @ViewDebug.IntToString(from = ALIGN_PARENT_END, to = "alignParentEnd"),
                @ViewDebug.IntToString(from = START_OF, to = "startOf"),
                @ViewDebug.IntToString(from = END_OF, to = "endOf")
        }, mapping = {
                @ViewDebug.IntToString(from = TRUE, to = "true"),
                @ViewDebug.IntToString(from = 0, to = "false/NO_ID")
        })

        private int[] mRules = new int[VERB_COUNT];
        private int[] mInitialRules = new int[VERB_COUNT];

        private int mLeft, mTop, mRight, mBottom;

        /**
         * Whether this view had any relative rules modified following the most
         * recent resolution of layout direction.
         */
        private boolean mNeedsLayoutResolution;

        private boolean mRulesChanged = false;
        private boolean mIsRtlCompatibilityMode = false;

        /**
         * When true, uses the parent as the anchor if the anchor doesn't exist or if
         * the anchor's visibility is GONE.
         */
        @ViewDebug.ExportedProperty(category = "layout")
        public boolean alignWithParent;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs,
                    com.android.internal.R.styleable.RelativeLayout_Layout);

            final int targetSdkVersion = c.getApplicationInfo().targetSdkVersion;
            mIsRtlCompatibilityMode = (targetSdkVersion < JELLY_BEAN_MR1 ||
                    !c.getApplicationInfo().hasRtlSupport());

            final int[] rules = mRules;
            //noinspection MismatchedReadAndWriteOfArray
            final int[] initialRules = mInitialRules;

            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignWithParentIfMissing:
                        alignWithParent = a.getBoolean(attr, false);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_toLeftOf:
                        rules[LEFT_OF] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_toRightOf:
                        rules[RIGHT_OF] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_above:
                        rules[ABOVE] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_below:
                        rules[BELOW] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignBaseline:
                        rules[ALIGN_BASELINE] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignLeft:
                        rules[ALIGN_LEFT] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignTop:
                        rules[ALIGN_TOP] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignRight:
                        rules[ALIGN_RIGHT] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignBottom:
                        rules[ALIGN_BOTTOM] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignParentLeft:
                        rules[ALIGN_PARENT_LEFT] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignParentTop:
                        rules[ALIGN_PARENT_TOP] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignParentRight:
                        rules[ALIGN_PARENT_RIGHT] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignParentBottom:
                        rules[ALIGN_PARENT_BOTTOM] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_centerInParent:
                        rules[CENTER_IN_PARENT] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_centerHorizontal:
                        rules[CENTER_HORIZONTAL] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_centerVertical:
                        rules[CENTER_VERTICAL] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_toStartOf:
                        rules[START_OF] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_toEndOf:
                        rules[END_OF] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignStart:
                        rules[ALIGN_START] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignEnd:
                        rules[ALIGN_END] = a.getResourceId(attr, 0);
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignParentStart:
                        rules[ALIGN_PARENT_START] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                    case com.android.internal.R.styleable.RelativeLayout_Layout_layout_alignParentEnd:
                        rules[ALIGN_PARENT_END] = a.getBoolean(attr, false) ? TRUE : 0;
                        break;
                }
            }
            mRulesChanged = true;
            System.arraycopy(rules, LEFT_OF, initialRules, LEFT_OF, VERB_COUNT);

            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        /**
         * Copy constructor. Clones the width, height, margin values, and rules
         * of the source.
         *
         * @param source The layout params to copy from.
         */
        public LayoutParams(LayoutParams source) {
            super(source);

            this.mIsRtlCompatibilityMode = source.mIsRtlCompatibilityMode;
            this.mRulesChanged = source.mRulesChanged;
            this.alignWithParent = source.alignWithParent;

            System.arraycopy(source.mRules, LEFT_OF, this.mRules, LEFT_OF, VERB_COUNT);
            System.arraycopy(
                    source.mInitialRules, LEFT_OF, this.mInitialRules, LEFT_OF, VERB_COUNT);
        }

        @Override
        public String debug(String output) {
            return output + "ViewGroup.LayoutParams={ width=" + sizeToString(width) +
                    ", height=" + sizeToString(height) + " }";
        }

        /**
         * Adds a layout rule to be interpreted by the RelativeLayout.
         * <p>
         * This method should only be used for verbs that don't refer to a
         * sibling (ex. {@link #ALIGN_RIGHT}) or take a boolean
         * value ({@link #TRUE} for true or 0 for false). To
         * specify a verb that takes a subject, use {@link #addRule(int, int)}.
         * <p>
         * If the rule is relative to the layout direction (ex.
         * {@link #ALIGN_PARENT_START}), then the layout direction must be
         * resolved using {@link #resolveLayoutDirection(int)} before calling
         * {@link #getRule(int)} an absolute rule (ex.
         * {@link #ALIGN_PARENT_LEFT}.
         *
         * @param verb a layout verb, such as {@link #ALIGN_PARENT_LEFT}
         * @see #addRule(int, int)
         * @see #removeRule(int)
         * @see #getRule(int)
         */
        public void addRule(int verb) {
            addRule(verb, TRUE);
        }

        /**
         * Adds a layout rule to be interpreted by the RelativeLayout.
         * <p>
         * Use this for verbs that refer to a sibling (ex.
         * {@link #ALIGN_RIGHT}) or take a boolean value (ex.
         * {@link #CENTER_IN_PARENT}).
         * <p>
         * If the rule is relative to the layout direction (ex.
         * {@link #START_OF}), then the layout direction must be resolved using
         * {@link #resolveLayoutDirection(int)} before calling
         * {@link #getRule(int)} with an absolute rule (ex. {@link #LEFT_OF}.
         *
         * @param verb    a layout verb, such as {@link #ALIGN_RIGHT}
         * @param subject the ID of another view to use as an anchor, or a
         *                boolean value (represented as {@link #TRUE} for true
         *                or 0 for false)
         * @see #addRule(int)
         * @see #removeRule(int)
         * @see #getRule(int)
         */
        public void addRule(int verb, int subject) {
            // If we're removing a relative rule, we'll need to force layout
            // resolution the next time it's requested.
            if (!mNeedsLayoutResolution && isRelativeRule(verb)
                    && mInitialRules[verb] != 0 && subject == 0) {
                mNeedsLayoutResolution = true;
            }

            mRules[verb] = subject;
            mInitialRules[verb] = subject;
            mRulesChanged = true;
        }

        /**
         * Removes a layout rule to be interpreted by the RelativeLayout.
         * <p>
         * If the rule is relative to the layout direction (ex.
         * {@link #START_OF}, {@link #ALIGN_PARENT_START}, etc.) then the
         * layout direction must be resolved using
         * {@link #resolveLayoutDirection(int)} before before calling
         * {@link #getRule(int)} with an absolute rule (ex. {@link #LEFT_OF}.
         *
         * @param verb One of the verbs defined by
         *             {@link android.widget.RelativeLayout RelativeLayout}, such as
         *             ALIGN_WITH_PARENT_LEFT.
         * @see #addRule(int)
         * @see #addRule(int, int)
         * @see #getRule(int)
         */
        public void removeRule(int verb) {
            addRule(verb, 0);
        }

        /**
         * Returns the layout rule associated with a specific verb.
         *
         * @param verb one of the verbs defined by {@link RelativeLayout}, such
         *             as ALIGN_WITH_PARENT_LEFT
         * @return the id of another view to use as an anchor, a boolean value
         * (represented as {@link RelativeLayout#TRUE} for true
         * or 0 for false), or -1 for verbs that don't refer to another
         * sibling (for example, ALIGN_WITH_PARENT_BOTTOM)
         * @see #addRule(int)
         * @see #addRule(int, int)
         */
        public int getRule(int verb) {
            return mRules[verb];
        }

        private boolean hasRelativeRules() {
            return (mInitialRules[START_OF] != 0 || mInitialRules[END_OF] != 0 ||
                    mInitialRules[ALIGN_START] != 0 || mInitialRules[ALIGN_END] != 0 ||
                    mInitialRules[ALIGN_PARENT_START] != 0 || mInitialRules[ALIGN_PARENT_END] != 0);
        }

        private boolean isRelativeRule(int rule) {
            return rule == START_OF || rule == END_OF
                    || rule == ALIGN_START || rule == ALIGN_END
                    || rule == ALIGN_PARENT_START || rule == ALIGN_PARENT_END;
        }

        // The way we are resolving rules depends on the layout direction and if we are pre JB MR1
        // or not.
        //
        // If we are pre JB MR1 (said as "RTL compatibility mode"), "left"/"right" rules are having
        // predominance over any "start/end" rules that could have been defined. A special case:
        // if no "left"/"right" rule has been defined and "start"/"end" rules are defined then we
        // resolve those "start"/"end" rules to "left"/"right" respectively.
        //
        // If we are JB MR1+, then "start"/"end" rules are having predominance over "left"/"right"
        // rules. If no "start"/"end" rule is defined then we use "left"/"right" rules.
        //
        // In all cases, the result of the resolution should clear the "start"/"end" rules to leave
        // only the "left"/"right" rules at the end.
        private void resolveRules(int layoutDirection) {
            final boolean isLayoutRtl = (layoutDirection == View.LAYOUT_DIRECTION_RTL);

            // Reset to initial state
            System.arraycopy(mInitialRules, LEFT_OF, mRules, LEFT_OF, VERB_COUNT);

            // Apply rules depending on direction and if we are in RTL compatibility mode
            if (mIsRtlCompatibilityMode) {
                if (mRules[ALIGN_START] != 0) {
                    if (mRules[ALIGN_LEFT] == 0) {
                        // "left" rule is not defined but "start" rule is: use the "start" rule as
                        // the "left" rule
                        mRules[ALIGN_LEFT] = mRules[ALIGN_START];
                    }
                    mRules[ALIGN_START] = 0;
                }

                if (mRules[ALIGN_END] != 0) {
                    if (mRules[ALIGN_RIGHT] == 0) {
                        // "right" rule is not defined but "end" rule is: use the "end" rule as the
                        // "right" rule
                        mRules[ALIGN_RIGHT] = mRules[ALIGN_END];
                    }
                    mRules[ALIGN_END] = 0;
                }

                if (mRules[START_OF] != 0) {
                    if (mRules[LEFT_OF] == 0) {
                        // "left" rule is not defined but "start" rule is: use the "start" rule as
                        // the "left" rule
                        mRules[LEFT_OF] = mRules[START_OF];
                    }
                    mRules[START_OF] = 0;
                }

                if (mRules[END_OF] != 0) {
                    if (mRules[RIGHT_OF] == 0) {
                        // "right" rule is not defined but "end" rule is: use the "end" rule as the
                        // "right" rule
                        mRules[RIGHT_OF] = mRules[END_OF];
                    }
                    mRules[END_OF] = 0;
                }

                if (mRules[ALIGN_PARENT_START] != 0) {
                    if (mRules[ALIGN_PARENT_LEFT] == 0) {
                        // "left" rule is not defined but "start" rule is: use the "start" rule as
                        // the "left" rule
                        mRules[ALIGN_PARENT_LEFT] = mRules[ALIGN_PARENT_START];
                    }
                    mRules[ALIGN_PARENT_START] = 0;
                }

                if (mRules[ALIGN_PARENT_END] != 0) {
                    if (mRules[ALIGN_PARENT_RIGHT] == 0) {
                        // "right" rule is not defined but "end" rule is: use the "end" rule as the
                        // "right" rule
                        mRules[ALIGN_PARENT_RIGHT] = mRules[ALIGN_PARENT_END];
                    }
                    mRules[ALIGN_PARENT_END] = 0;
                }
            } else {
                // JB MR1+ case
                if ((mRules[ALIGN_START] != 0 || mRules[ALIGN_END] != 0) &&
                        (mRules[ALIGN_LEFT] != 0 || mRules[ALIGN_RIGHT] != 0)) {
                    // "start"/"end" rules take precedence over "left"/"right" rules
                    mRules[ALIGN_LEFT] = 0;
                    mRules[ALIGN_RIGHT] = 0;
                }
                if (mRules[ALIGN_START] != 0) {
                    // "start" rule resolved to "left" or "right" depending on the direction
                    mRules[isLayoutRtl ? ALIGN_RIGHT : ALIGN_LEFT] = mRules[ALIGN_START];
                    mRules[ALIGN_START] = 0;
                }
                if (mRules[ALIGN_END] != 0) {
                    // "end" rule resolved to "left" or "right" depending on the direction
                    mRules[isLayoutRtl ? ALIGN_LEFT : ALIGN_RIGHT] = mRules[ALIGN_END];
                    mRules[ALIGN_END] = 0;
                }

                if ((mRules[START_OF] != 0 || mRules[END_OF] != 0) &&
                        (mRules[LEFT_OF] != 0 || mRules[RIGHT_OF] != 0)) {
                    // "start"/"end" rules take precedence over "left"/"right" rules
                    mRules[LEFT_OF] = 0;
                    mRules[RIGHT_OF] = 0;
                }
                if (mRules[START_OF] != 0) {
                    // "start" rule resolved to "left" or "right" depending on the direction
                    mRules[isLayoutRtl ? RIGHT_OF : LEFT_OF] = mRules[START_OF];
                    mRules[START_OF] = 0;
                }
                if (mRules[END_OF] != 0) {
                    // "end" rule resolved to "left" or "right" depending on the direction
                    mRules[isLayoutRtl ? LEFT_OF : RIGHT_OF] = mRules[END_OF];
                    mRules[END_OF] = 0;
                }

                if ((mRules[ALIGN_PARENT_START] != 0 || mRules[ALIGN_PARENT_END] != 0) &&
                        (mRules[ALIGN_PARENT_LEFT] != 0 || mRules[ALIGN_PARENT_RIGHT] != 0)) {
                    // "start"/"end" rules take precedence over "left"/"right" rules
                    mRules[ALIGN_PARENT_LEFT] = 0;
                    mRules[ALIGN_PARENT_RIGHT] = 0;
                }
                if (mRules[ALIGN_PARENT_START] != 0) {
                    // "start" rule resolved to "left" or "right" depending on the direction
                    mRules[isLayoutRtl ? ALIGN_PARENT_RIGHT : ALIGN_PARENT_LEFT] = mRules[ALIGN_PARENT_START];
                    mRules[ALIGN_PARENT_START] = 0;
                }
                if (mRules[ALIGN_PARENT_END] != 0) {
                    // "end" rule resolved to "left" or "right" depending on the direction
                    mRules[isLayoutRtl ? ALIGN_PARENT_LEFT : ALIGN_PARENT_RIGHT] = mRules[ALIGN_PARENT_END];
                    mRules[ALIGN_PARENT_END] = 0;
                }
            }

            mRulesChanged = false;
            mNeedsLayoutResolution = false;
        }

        /**
         * Retrieves a complete list of all supported rules, where the index is the rule
         * verb, and the element value is the value specified, or "false" if it was never
         * set. If there are relative rules defined (*_START / *_END), they will be resolved
         * depending on the layout direction.
         *
         * @param layoutDirection the direction of the layout.
         *                        Should be either {@link View#LAYOUT_DIRECTION_LTR}
         *                        or {@link View#LAYOUT_DIRECTION_RTL}
         * @return the supported rules
         * @hide
         * @see #addRule(int, int)
         */
        public int[] getRules(int layoutDirection) {
            resolveLayoutDirection(layoutDirection);
            return mRules;
        }

        /**
         * Retrieves a complete list of all supported rules, where the index is the rule
         * verb, and the element value is the value specified, or "false" if it was never
         * set. There will be no resolution of relative rules done.
         *
         * @return the supported rules
         * @see #addRule(int, int)
         */
        public int[] getRules() {
            return mRules;
        }

        /**
         * This will be called by {@link android.view.View#requestLayout()} to
         * resolve layout parameters that are relative to the layout direction.
         * <p>
         * After this method is called, any rules using layout-relative verbs
         * (ex. {@link #START_OF}) previously added via {@link #addRule(int)}
         * may only be accessed via their resolved absolute verbs (ex.
         * {@link #LEFT_OF}).
         */
        @Override
        public void resolveLayoutDirection(int layoutDirection) {
            if (shouldResolveLayoutDirection(layoutDirection)) {
                resolveRules(layoutDirection);
            }

            // This will set the layout direction.
            super.resolveLayoutDirection(layoutDirection);
        }

        private boolean shouldResolveLayoutDirection(int layoutDirection) {
            return (mNeedsLayoutResolution || hasRelativeRules())
                    && (mRulesChanged || layoutDirection != getLayoutDirection());
        }

        /**
         * @hide
         */
        @Override
        protected void encodeProperties(@NonNull ViewHierarchyEncoder encoder) {
            super.encodeProperties(encoder);
            encoder.addProperty("layout:alignWithParent", alignWithParent);
        }
    }

    private static class DependencyGraph {
        /**
         * List of all views in the graph.
         */
        private ArrayList<Node> mNodes = new ArrayList<Node>();

        /**
         * List of nodes in the graph. Each node is identified by its
         * view id (see View#getId()).
         */
        private SparseArray<Node> mKeyNodes = new SparseArray<Node>();

        /**
         * Temporary data structure used to build the list of roots
         * for this graph.
         */
        private ArrayDeque<Node> mRoots = new ArrayDeque<Node>();

        /**
         * Clears the graph.
         */
        void clear() {
            final ArrayList<Node> nodes = mNodes;
            final int count = nodes.size();

            for (int i = 0; i < count; i++) {
                nodes.get(i).release();
            }
            nodes.clear();

            mKeyNodes.clear();
            mRoots.clear();
        }

        /**
         * Adds a view to the graph.
         *
         * @param view The view to be added as a node to the graph.
         */
        void add(View view) {
            final int id = view.getId();
            final Node node = Node.acquire(view);

            if (id != View.NO_ID) {
                mKeyNodes.put(id, node);
            }

            mNodes.add(node);
        }

        /**
         * Builds a sorted list of views. The sorting order depends on the dependencies
         * between the view. For instance, if view C needs view A to be processed first
         * and view A needs view B to be processed first, the dependency graph
         * is: B -> A -> C. The sorted array will contain views B, A and C in this order.
         *
         * @param sorted The sorted list of views. The length of this array must
         *               be equal to getChildCount().
         * @param rules  The list of rules to take into account.
         */
        void getSortedViews(View[] sorted, int... rules) {
            // 得到所有的根节点
            final ArrayDeque<Node> roots = findRoots(rules);
            int index = 0;

            Node node;
            // 遍历根节点
            while ((node = roots.pollLast()) != null) {
                final View view = node.view;
                // 找到view的id
                final int key = view.getId();
                // 保存view到数组中
                sorted[index++] = view;
                // 得到这个根节点的依赖关系
                final ArrayMap<Node, DependencyGraph> dependents = node.dependents;
                final int count = dependents.size();
                // 遍历所有依赖了根节点的关系
                for (int i = 0; i < count; i++) {
                    // 找到依赖根节点的 节点
                    final Node dependent = dependents.keyAt(i);
                    // 找到依赖关系的集合
                    final SparseArray<Node> dependencies = dependent.dependencies;
                    // 移除依赖关系
                    dependencies.remove(key);
                    // 如果已经没有任何依赖关系，加入到根节点中
                    if (dependencies.size() == 0) {
                        roots.add(dependent);
                    }
                }
            }

            if (index < sorted.length) {
                throw new IllegalStateException("Circular dependencies cannot exist"
                        + " in RelativeLayout");
            }
        }

        /**
         * Finds the roots of the graph. A root is a node with no dependency and
         * with [0..n] dependents.
         *
         * @param rulesFilter The list of rules to consider when building the
         *        dependencies
         *
         * @return A list of node, each being a root of the graph
         */
        /**
         * 找到所有的root节点，也就是没有依赖关系的节点
         */
        private ArrayDeque<Node> findRoots(int[] rulesFilter) {
            // mKeyNodes 表示定了id的节点
            final SparseArray<Node> keyNodes = mKeyNodes;
            // 所有的节点
            final ArrayList<Node> nodes = mNodes;
            final int count = nodes.size();

            // 把之前保存的节点的依赖关系全部清空
            for (int i = 0; i < count; i++) {
                final Node node = nodes.get(i);
                node.dependents.clear();
                node.dependencies.clear();
            }

            // 遍历所有的child
            for (int i = 0; i < count; i++) {
                final Node node = nodes.get(i);
                // 得到节点的view的布局信息
                final LayoutParams layoutParams = (LayoutParams) node.view.getLayoutParams();
                // 得到依赖的规则
                final int[] rules = layoutParams.mRules;
                final int rulesCount = rulesFilter.length;

                // 遍历这个child的所有的依赖规则
                for (int j = 0; j < rulesCount; j++) {
                    // 过滤出需要匹配的规则，rule就是id
                    final int rule = rules[rulesFilter[j]];
                    if (rule > 0) {
                        // 找到指定id的节点
                        final Node dependency = keyNodes.get(rule);
                        if (dependency == null || dependency == node) {
                            continue;
                        }
                        // 绑定关键节点 和 这个节点的关系
                        dependency.dependents.put(node, this);
                        // 把rule和对应的保存起来保存到
                        node.dependencies.put(rule, dependency);
                    }
                }
            }

            final ArrayDeque<Node> roots = mRoots;
            roots.clear();

            // 把所有没有依赖关系的根节点保存成数组返回
            for (int i = 0; i < count; i++) {
                final Node node = nodes.get(i);
                if (node.dependencies.size() == 0) roots.addLast(node);
            }

            return roots;
        }

        /**
         * A node in the dependency graph. A node is a view, its list of dependencies
         * and its list of dependents.
         * <p>
         * A node with no dependent is considered a root of the graph.
         */
        static class Node {
            /**
             * The view representing this node in the layout.
             */
            View view;

            /**
             * The list of dependents for this node; a dependent is a node
             * that needs this node to be processed first.
             */
            final ArrayMap<Node, DependencyGraph> dependents =
                    new ArrayMap<Node, DependencyGraph>();

            /**
             * The list of dependencies for this node.
             */
            final SparseArray<Node> dependencies = new SparseArray<Node>();

            /*
             * START POOL IMPLEMENTATION
             */
            // The pool is static, so all nodes instances are shared across
            // activities, that's why we give it a rather high limit
            private static final int POOL_LIMIT = 100;
            private static final SynchronizedPool<Node> sPool =
                    new SynchronizedPool<Node>(POOL_LIMIT);

            static Node acquire(View view) {
                Node node = sPool.acquire();
                if (node == null) {
                    node = new Node();
                }
                node.view = view;
                return node;
            }

            void release() {
                view = null;
                dependents.clear();
                dependencies.clear();

                sPool.release(this);
            }
            /*
             * END POOL IMPLEMENTATION
             */
        }
    }
}
