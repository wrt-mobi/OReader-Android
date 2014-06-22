package mobi.wrt.oreader.app.view.listeners;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;

public class FloatHeaderScrollListener implements AbsListView.OnScrollListener {

    private final View floatHeaderShadowView;
    private int currentTopMargin;

    private int lastVisibleItem = -1;

    private boolean isShortVariantShown = false;

    private View listViewHeaderFakeView;

    private View floatHeaderView;

    private int headerHeight;

    private int headerHeightMin;

    public FloatHeaderScrollListener(View listViewHeaderFakeView, View floatHeaderView, View floatHeaderShadowView, int headerHeight, int headerHeightMin) {
        this.listViewHeaderFakeView = listViewHeaderFakeView;
        this.headerHeight = headerHeight;
        this.headerHeightMin = headerHeightMin;
        this.floatHeaderView = floatHeaderView;
        this.floatHeaderShadowView = floatHeaderShadowView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (lastVisibleItem == -1) {
            lastVisibleItem = firstVisibleItem;
        }
        try {
            if (lastVisibleItem <= firstVisibleItem) {
                //scroll to bottom
                if (firstVisibleItem == 0) {
                    //full header visible
                    int bottom = listViewHeaderFakeView.getBottom();
                    int bottomValue = bottom - view.getPaddingTop();
                    int topMargin = firstVisibleItem == 0 ? -(headerHeight - bottomValue) : -headerHeight;
                    if (isShortVariantShown && topMargin < currentTopMargin) {
                        floatHeaderShadowView.setVisibility(View.VISIBLE);
                        return;
                    }
                    isShortVariantShown = false;
                    updateHeaderMargin(topMargin, false);
                    floatHeaderShadowView.setVisibility(View.GONE);
                } else {
                    //header is not visible can ignore or hide if shown short variant of header
                    if (isShortVariantShown && lastVisibleItem == firstVisibleItem) {
                        floatHeaderShadowView.setVisibility(View.VISIBLE);
                        return;
                    }
                    floatHeaderShadowView.setVisibility(View.GONE);
                    isShortVariantShown = false;
                    int topMargin = -headerHeight;
                    updateHeaderMargin(topMargin, true);
                }
            } else {
                //scroll to top
                if (firstVisibleItem > 0) {
                    //show short variant
                    isShortVariantShown = true;
                    int topMargin = headerHeightMin - headerHeight;
                    updateHeaderMargin(topMargin, true);
                    floatHeaderShadowView.setVisibility(View.VISIBLE);
                } else {
                    //full header visible
                    int bottom = listViewHeaderFakeView.getBottom();
                    int bottomValue = bottom - view.getPaddingTop();
                    int topMargin = firstVisibleItem == 0 ? -(headerHeight - bottomValue) : -headerHeight;
                    if (isShortVariantShown && topMargin < currentTopMargin) {
                        floatHeaderShadowView.setVisibility(View.VISIBLE);
                        return;
                    }
                    isShortVariantShown = false;
                    updateHeaderMargin(topMargin, false);
                    floatHeaderShadowView.setVisibility(View.GONE);
                }
            }
        } finally {
            lastVisibleItem = firstVisibleItem;
        }
    }

    private Animation currentAnimation;

    public void updateHeaderMargin(final int newTopMargin, final boolean isAnimate) {
        try {
            if (currentTopMargin == newTopMargin) {
                return;
            }
            if (currentAnimation != null) {
                currentAnimation.cancel();
                currentAnimation = null;
            }
            int abs = Math.abs(currentTopMargin + (-newTopMargin));
            if (!isAnimate || abs < headerHeightMin) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) floatHeaderView.getLayoutParams();
                layoutParams.topMargin = newTopMargin;
                floatHeaderView.setLayoutParams(layoutParams);
                return;
            }
            currentAnimation = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) floatHeaderView.getLayoutParams();
                    if (isAnimate) {
                        int newValue = (int) ((newTopMargin - layoutParams.topMargin) * interpolatedTime);
                        layoutParams.topMargin = layoutParams.topMargin + newValue;
                    } else {
                        layoutParams.topMargin = (int) (newTopMargin * interpolatedTime);
                    }
                    floatHeaderView.setLayoutParams(layoutParams);
                }
            };
            if (isAnimate) {
                currentAnimation.setDuration(300l);
            }
            floatHeaderView.startAnimation(currentAnimation);
        } finally {
            currentTopMargin = newTopMargin;
        }
    }

}
