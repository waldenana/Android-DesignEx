package android.support.design.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.anzewei.design.R;

import java.lang.reflect.Field;

/**
 * Parallax for AppBarLayout
 * Created by zewei on 2015-12-25.
 */
public class ParallaxScaleBehavior extends AppBarLayout.Behavior {
    private ViewOffsetHelper mTopHelper;
    private View mContent;
    private ValueAnimatorCompat mAnimator;
    private static final int ANIMATE_PER_SECOND = 300;
    private Field mOffset;
    private CollapsingToolbarLayout mToolLayout;

    public ParallaxScaleBehavior() {
        super();
    }

    public ParallaxScaleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        if (mTopHelper == null) {
            mContent = getParallaxView(abl);
            if (mContent != null) {
                mTopHelper = new ViewOffsetHelper(mContent);
                clipChild(parent, mContent);
            }
        }
        if (mToolLayout == null) {
            mToolLayout = getCollapsingToolbarLayout(abl);
        }
        if (mOffset == null) {
            getField();
        }
        return super.onLayoutChild(parent, abl, layoutDirection);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        return getTopAndBottomOffset() > 0 //if has scale don't fling
                || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, AppBarLayout header, int newOffset, int minOffset, int maxOffset) {
        header.setClipChildren(newOffset <= 0);
        int oldTop = getTopAndBottomOffset();
        if (oldTop > 0 || newOffset >= 0) {//fling start
            // start parallax
            return overScroll(coordinatorLayout, header, newOffset, getMaxRange());
        }
        int scale = super.setHeaderTopBottomOffset(coordinatorLayout, header, newOffset, minOffset, maxOffset);
        if (scale == 0 && newOffset > 0 && maxOffset == 0) {
            // start parallax
            scale = overScroll(coordinatorLayout, header, newOffset, getMaxRange());
        } else {//stop parallax
            scaleContent(1);
            header.setClipChildren(true);
        }
        return scale;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        boolean shouldInterrupt = MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_UP && getTopAndBottomOffset() > 0;
        super.onTouchEvent(parent, child, ev);
        //if has scale abort filing,because HeaderBehavior invoked fling maxY=0,it is error
        if (shouldInterrupt) {
            if (mScroller != null)
                mScroller.abortAnimation();
            animateOffsetTo(parent,child,0);
        }
        return true;
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        boolean started = super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
        if (started && mAnimator != null) {
            // Cancel any offset animation
            mAnimator.cancel();
        }
        return started;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
        if (getTopAndBottomOffset() > 0) {
            animateOffsetTo(coordinatorLayout, abl, 0);
        } else {
            super.onStopNestedScroll(coordinatorLayout, abl, target);
        }
    }

    /**
     * @return 下拉的最大值
     */
    private int getMaxRange() {
        return mContent != null ? mContent.getHeight() / 2 : 0;
    }

    /**
     * @return CoordinatorLayout 开始下拉缩放
     */
    private int overScroll(CoordinatorLayout coordinatorLayout, AppBarLayout header, int newOffset, int maxOffset) {
        if (newOffset > maxOffset || mTopHelper == null)
            return 0;
        int c = super.setHeaderTopBottomOffset(coordinatorLayout, header, newOffset, Integer.MIN_VALUE, maxOffset);
        if (c != 0) {
            int top = getTopAndBottomOffset();
            layoutAppbar(mToolLayout, -top);
            float scale = newOffset == 0 ? 1 : (float) (header.getHeight() + newOffset + 2) / (float) header.getHeight();
            mTopHelper.setTopAndBottomOffset(-top / 2);
            scaleContent(scale);
            if (mOffset != null) {
                try {
                    mOffset.set(mToolLayout, top);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return c;
    }

    /**
     * @param value Image的缩放值
     */
    private void scaleContent(float value) {
        value = Math.max(1, value);
        ViewCompat.setScaleY(mContent, value);
        ViewCompat.setScaleX(mContent, value);
    }

    @Override
    public int getTopAndBottomOffset() {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        if (stackTrace.getMethodName().equals("dispatchOffsetUpdates"))
            return Math.min(0, super.getTopAndBottomOffset());
        return super.getTopAndBottomOffset();
    }

    /**
     * @param appBarLayout appBar
     * @return 要进行缩放的View
     */
    private View getParallaxView(AppBarLayout appBarLayout) {
        if (appBarLayout.getChildCount() > 0) {
            View view = appBarLayout.getChildAt(0);
            if (view instanceof CollapsingToolbarLayout) {
                int count = ((CollapsingToolbarLayout) view).getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = ((CollapsingToolbarLayout) view).getChildAt(i);
                    CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) child.getLayoutParams();
                    if (params.getCollapseMode() == CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX)
                        return child;
                }
            }
        }
        return appBarLayout.findViewById(R.id.image);
    }

    /**
     * 放置CollapsingToolbarLayout的child
     *
     * @param appBarLayout appBarLayout
     * @param offset       偏移值
     */
    private void layoutAppbar(CollapsingToolbarLayout appBarLayout, int offset) {
        if (appBarLayout == null)
            return;
        int count = appBarLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = appBarLayout.getChildAt(i);
            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) view.getLayoutParams();
            if (params.getCollapseMode() == CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN) {
                ViewOffsetHelper offsetHelper = getViewOffsetHelper(view);
                offsetHelper.setTopAndBottomOffset(offset);
            }
        }
        Drawable drawable = appBarLayout.getContentScrim();
        if (drawable != null) {
            drawable.getBounds().top = offset > 0 ? 0 : offset;
        }
    }

    private void clipChild(CoordinatorLayout layout, View content) {
        ViewGroup viewGroup = (ViewGroup) content.getParent();
        while (viewGroup != layout) {
            viewGroup.setClipChildren(false);
            viewGroup = (ViewGroup) viewGroup.getParent();
        }
        layout.setClipChildren(false);
    }

    private static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(android.support.design.R.id.view_offset_helper);
        if (offsetHelper == null) {
            offsetHelper = new ViewOffsetHelper(view);
            view.setTag(android.support.design.R.id.view_offset_helper, offsetHelper);
        }
        return offsetHelper;
    }

    private CollapsingToolbarLayout getCollapsingToolbarLayout(AppBarLayout appBarLayout) {
        int count = appBarLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View c = appBarLayout.getChildAt(i);
            if (c instanceof CollapsingToolbarLayout)
                return (CollapsingToolbarLayout) c;
        }
        return null;
    }

    private void animateOffsetTo(final CoordinatorLayout coordinatorLayout,
                                 final AppBarLayout child, final int offset) {
        final int currentOffset = getTopBottomOffsetForScrollingSibling();
        if (currentOffset == offset) {
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.cancel();
            }
            return;
        }

        if (mAnimator == null) {
            mAnimator = ViewUtils.createAnimator();
            mAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
            mAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimatorCompat animation) {
                    overScroll(coordinatorLayout, child,
                            animation.getAnimatedIntValue(), getMaxRange());
                }
            });
        } else {
            mAnimator.cancel();
        }

        // Set the duration based on the amount of dips we're travelling in
        final float distanceDp = Math.abs(currentOffset - offset) /
                coordinatorLayout.getResources().getDisplayMetrics().density;
        mAnimator.setDuration(Math.round(distanceDp * 1000 / ANIMATE_PER_SECOND));

        mAnimator.setIntValues(currentOffset, offset);
        mAnimator.start();
    }

    private void getField() {
        try {
            Field personNameField = CollapsingToolbarLayout.class.getDeclaredField("mCurrentOffset");
            personNameField.setAccessible(true);
            mOffset = personNameField;
        } catch (Exception e) {
        }
    }
}
