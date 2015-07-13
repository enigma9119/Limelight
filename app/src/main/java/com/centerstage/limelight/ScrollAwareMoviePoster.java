package com.centerstage.limelight;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Smitesh on 7/12/2015.
 * Code derived and modified from Android Open Source internal FloatingActionButton class.
 */
public class ScrollAwareMoviePoster extends CoordinatorLayout.Behavior<View> {

    private boolean mIsAnimatingOut;
    private FastOutSlowInInterpolator fastOutSlowInInterpolator = new FastOutSlowInInterpolator();

    public ScrollAwareMoviePoster(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            AppBarLayout appBarLayout = (AppBarLayout) dependency;

            Rect tmpRect = new Rect();
            tmpRect.set(0, 0, dependency.getWidth(), dependency.getHeight());
            parent.offsetDescendantRectToMyCoords(dependency, tmpRect);

            if (tmpRect.bottom <= getMinimumHeightForVisibleOverlappingContent(appBarLayout)) {
                if(!mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
                    animateOut(child);
                }
            } else {
                animateIn(child);
            }
        }

        return false;
    }

    private int getMinimumHeightForVisibleOverlappingContent(AppBarLayout appBarLayout) {
        int minHeight = ViewCompat.getMinimumHeight(appBarLayout);
        if(minHeight != 0) {
            return minHeight*2;
        } else {
            int childCount = appBarLayout.getChildCount();
            return childCount >= 1? ViewCompat.getMinimumHeight(appBarLayout.getChildAt(childCount - 1)) * 2 : 0;
        }
    }

    private void animateIn(View view) {
        view.setVisibility(View.VISIBLE);
        ViewCompat.animate(view)
                .scaleX(1.0F)
                .scaleY(1.0F)
                .alpha(1.0F)
                .setInterpolator(fastOutSlowInInterpolator)
                .withLayer()
                .setListener(null).start();
    }

    private void animateOut(final View view) {
        ViewCompat.animate(view)
                .scaleX(0.0F)
                .scaleY(0.0F)
                .alpha(0.0F)
                .setInterpolator(fastOutSlowInInterpolator)
                .withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        mIsAnimatingOut = true;
                    }

                    public void onAnimationCancel(View view) {
                        mIsAnimatingOut = false;
                    }

                    public void onAnimationEnd(View view) {
                        mIsAnimatingOut = false;
                        view.setVisibility(View.GONE);
                    }
                }).start();
    }
}
