package com.aliya.bannerview.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.aliya.view.banner.BannerView;
import com.aliya.view.banner.view.BannerViewPager;

/**
 * XBannerView
 *
 * @author a_liYa
 * @date 2020-02-21 15:55.
 */
public class XBannerView extends BannerView {

    private int mPageSelected;

    private Callback mCallback;

    public XBannerView(Context context) {
        this(context, null);
    }

    public XBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addOnPageChangeListener(new BannerViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageSelected = position;
                if (mCallback != null) {
                    mCallback.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public Callback getCallback() {
        return mCallback;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected void onPostDelayed(int delayMs) {
        super.onPostDelayed(delayMs);
        if (mCallback != null)
            mCallback.onPostDelayed(mPageSelected, delayMs);
    }

    @Override
    protected void onRemoveDelayed() {
        super.onRemoveDelayed();
        if (mCallback != null) {
            mCallback.onRemoveDelayed(mPageSelected);
        }
    }

    public interface Callback {
        void onPageSelected(int position);

        void onPostDelayed(int selectedPosition, int delayMs);

        void onRemoveDelayed(int selectedPosition);
    }
}
