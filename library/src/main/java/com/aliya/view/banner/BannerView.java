package com.aliya.view.banner;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;

import com.aliya.view.banner.view.BannerViewPager;
import com.aliya.view.banner.view.BannerViewPager.OnPageChangeListener;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.HashSet;
import java.util.Set;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 自定义Banner条
 *
 * @author a_liYa
 * @date 2016-4-18 下午4:57:29
 */
public class BannerView extends RelativeLayout {

    private BannerViewPager mViewPager;
    private Set<OnPageChangeListener> mOnPageChangeListeners = new HashSet<>();

    private int mAutoMs;                // 轮播间隔时间.
    private boolean auto = true;        // 是否自动轮播标志, true:自动.
    private boolean onStarted = true;   // 是否在生命周期 onStart() 和 onStop() 之间, 默认为true.
    private boolean isAttached = false; // this view is currently attached to a window.

    private int mPagerPaddingLeft;      // ViewPager#paddingLeft.
    private int mPagerPaddingRight;     // ViewPager#paddingRight.

    private int mItemCount;             // banner条目个数.

    private BannerPagerAdapter mAdapter;

    /**
     * 宽高的比率
     */
    private float ratio_w_h = -1;
    private static final String RATIO_SYMBOL = ":";

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mViewPager = new BannerViewPager(context);
        addView(mViewPager, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        if (attrs == null) return;

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.BannerView);

        { // 固定宽高比参数
            String w_h = ta.getString(R.styleable.BannerView_banner_w2h);
            if (!TextUtils.isEmpty(w_h) && w_h.contains(RATIO_SYMBOL)) {
                String[] split = w_h.trim().split(RATIO_SYMBOL);
                if (split != null && split.length == 2) {
                    try {
                        ratio_w_h = Float.parseFloat(split[0].trim())
                                / Float.parseFloat(split[1].trim());
                    } catch (NumberFormatException e) {
                        // no-op
                    }
                }
            }
        }

        auto = ta.getBoolean(R.styleable.BannerView_banner_isAuto, auto);
        mAutoMs = ta.getInteger(R.styleable.BannerView_banner_autoMs,
                context.getResources().getInteger(R.integer.banner_view_auto_duration));

        mPagerPaddingLeft =
                ta.getDimensionPixelSize(R.styleable.BannerView_banner_pagerPaddingLeft, 0);
        mPagerPaddingRight =
                ta.getDimensionPixelSize(R.styleable.BannerView_banner_pagerPaddingRight, 0);

        mViewPager.setPadding(mPagerPaddingLeft, 0, mPagerPaddingRight, 0);
        if (mPagerPaddingLeft > 0 || mPagerPaddingRight > 0) {
            mViewPager.setClipToPadding(false);
        }

        ta.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
        startAuto();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
        stopAuto();
    }

    /**
     * 获取当前item
     *
     * @return item position
     */
    public int getCurrentItem() {
        return mItemCount == 0 ? 0 : mViewPager.getCurrentItem() % mItemCount;
    }

    /**
     * Set the currently selected page.
     *
     * @param item         Item index to select
     * @param smoothScroll True to smoothly scroll to the new item, false to transition immediately
     */
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (item < 0 || item >= mItemCount) return;

        int currentItem = mViewPager.getCurrentItem();
        int modulus = currentItem % mItemCount;
        if (modulus > item) {
            if (modulus - item < mItemCount - (modulus - item)) { // 前面近
                mViewPager.setCurrentItem(currentItem - (modulus - item), smoothScroll);
            } else { // 后面近
                mViewPager.setCurrentItem(currentItem + mItemCount - (modulus - item),
                        smoothScroll);
            }
        } else if (modulus < item) {
            if (item - modulus > mItemCount - (item - modulus)) { // 前面近
                mViewPager.setCurrentItem(currentItem - mItemCount - (item - modulus),
                        smoothScroll);
            } else { // 后面近
                mViewPager.setCurrentItem(currentItem + item - modulus, smoothScroll);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio_w_h > 0) {
            int wMode = MeasureSpec.getMode(widthMeasureSpec);
            int hMode = MeasureSpec.getMode(heightMeasureSpec);

            int wSize = MeasureSpec.getSize(widthMeasureSpec)
                    - mPagerPaddingLeft - mPagerPaddingRight;
            int hSize = MeasureSpec.getSize(heightMeasureSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            if (wMode == MeasureSpec.EXACTLY && hMode != MeasureSpec.EXACTLY
                    || params.width != WRAP_CONTENT && params.height == WRAP_CONTENT) {
                heightMeasureSpec = MeasureSpec
                        .makeMeasureSpec(Math.round(wSize / ratio_w_h), MeasureSpec.EXACTLY);
            } else if (wMode != MeasureSpec.EXACTLY && hMode == MeasureSpec.EXACTLY
                    || params.width == WRAP_CONTENT && params.height != WRAP_CONTENT) {
                widthMeasureSpec = MeasureSpec
                        .makeMeasureSpec(Math.round(hSize * ratio_w_h), MeasureSpec.EXACTLY);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addOnPageChangeListener(OnPageChangeListener onBannerPageChangeListener) {
        mOnPageChangeListeners.add(onBannerPageChangeListener);
    }

    public void setAdapter(BannerPagerAdapter adapter) {
        if (mViewPager == null || adapter == null) {
            return;
        }
        if (mAdapter != null) {
            mViewPager.removeOnPageChangeListener(mOnPageChangeListener);
            stopAuto();
        }

        mAdapter = adapter;
        mItemCount = mAdapter.getTruthCount();

        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        if (mItemCount > 0 && mAdapter.isCanCycle()) {
            int median = mAdapter.getCount() / 2;
            mViewPager.setCurrentItem(median - median % mItemCount, false);
            // 防止首次不回调
            mOnPageChangeListener.onPageSelected(mViewPager.getCurrentItem() % mItemCount);
            startAuto();
        }

        if (mAdapterChangeListener != null) {
            mAdapterChangeListener.onAdapterChange();
        }

    }

    public BannerViewPager getViewPager() {
        return mViewPager;
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (mOnPageChangeListeners != null) {
                position = position % mItemCount;
                for (OnPageChangeListener iterable : mOnPageChangeListeners) {
                    iterable.onPageSelected(position);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            if (mOnPageChangeListeners != null) {
                position = position % mItemCount;
                for (OnPageChangeListener iterable : mOnPageChangeListeners) {
                    iterable.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListeners != null) {
                for (OnPageChangeListener iterable : mOnPageChangeListeners) {
                    iterable.onPageScrollStateChanged(state);
                }
            }

            // 滑动结束，开启定时器
            if (state == BannerViewPager.SCROLL_STATE_IDLE) {
                startAuto();
            } else { // 滑动中，停止定时器
                stopAuto();
            }
        }
    };

    public BannerPagerAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置是否自动轮播
     *
     * @param canAuto true；自动轮播；false：不能自动
     */
    public void setAuto(boolean canAuto) {
        if (auto = canAuto) {
            startAuto();
        } else {
            stopAuto();
        }
    }

    public boolean isAuto() {
        return auto;
    }

    /**
     * 设置轮播时间间隔
     *
     * @param autoMs 时间 ms
     */
    public void setAutoMs(int autoMs) {
        this.mAutoMs = autoMs;
    }

    /**
     * 生命周期，在{@link Activity#onStart()}或{@link Fragment#onStart()}调用
     */
    public final void onStart() {
        onStarted = true;
        startAuto();
    }

    /**
     * 生命周期，在{@link Activity#onStop()}或{@link Fragment#onStop()}调用
     */
    public final void onStop() {
        onStarted = false;
        stopAuto();
    }

    /**
     * 开始轮播, 如果条件均满足的话.
     */
    public final void startAuto() {
        removeCallbacks(mAutoRunnable);
        if (isCanAuto()) {
            postDelayed(mAutoRunnable, mAutoMs);
        }
    }

    /**
     * 停止轮播
     */
    public final void stopAuto() {
        removeCallbacks(mAutoRunnable);
    }

    /**
     * 判断当前否可以自动轮播
     *
     * @return true : 可以
     */
    private boolean isCanAuto() {
        return mAdapter != null
                && mAdapter.isCanCycle()
                && mItemCount > 0
                && auto
                && isAttached
                && onStarted;
    }

    private Runnable mAutoRunnable = new Runnable() {
        @Override
        public void run() {
            if (mViewPager == null || mAdapter == null) return;

            if (mViewPager.getCurrentItem() >= mAdapter.getCount()) {
                int middle = mAdapter.getCount() / 2;
                mViewPager.setCurrentItem(middle - middle % mItemCount, false);
            } else {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
            if (isCanAuto()) {
                postDelayed(mAutoRunnable, mAutoMs);
            }
        }
    };

    private OnAdapterChangeListener mAdapterChangeListener;

    void setAdapterChangeListener(OnAdapterChangeListener listener) {
        mAdapterChangeListener = listener;
    }


}
