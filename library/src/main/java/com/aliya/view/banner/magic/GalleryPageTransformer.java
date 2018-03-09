package com.aliya.view.banner.magic;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 实现画廊效果 - PageTransformer
 *
 * @author a_liYa
 * @date 2018/3/6 09:51.
 */
public class GalleryPageTransformer implements ViewPager.PageTransformer {

    private float mMaxScale;
    private float mMinScale;
    private int mPageMargin;
    private ViewPager mViewPager;

    public GalleryPageTransformer(ViewPager viewPager, int pageMargin,
                                  float maxScale, float minScale) {
        mMaxScale = maxScale;
        mMinScale = minScale;
        mPageMargin = pageMargin;
        mViewPager = viewPager;
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void transformPage(View page, float position) {
        final int marginPixels = Math.round(mPageMargin -
                (mMaxScale - mMinScale) * getClientWidth(mViewPager) / 2f);
        if (marginPixels != mViewPager.getPageMargin()) {
            mViewPager.post(new Runnable() {
                @Override
                public void run() {
                    mViewPager.setPageMargin(marginPixels);
                }
            });
        }

        if (page.getParent() instanceof View) {
            View parent = (ViewPager) page.getParent();
            int scrollX = parent.getScrollX();
            position = (float) (page.getLeft() - parent.getPaddingLeft() - scrollX) /
                    getClientWidth(parent);
        }

        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }

        // 范围 [0, 1]
        float progress = Math.abs(position);

        float scaleValue = mMaxScale - (mMaxScale - mMinScale) * progress;

        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            page.getParent().requestLayout();
        }

    }

    private float getClientWidth(View view) {
        return view.getMeasuredWidth() - view.getPaddingLeft() - view.getPaddingRight();
    }

}
