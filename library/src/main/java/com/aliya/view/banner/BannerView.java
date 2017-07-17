package com.aliya.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义Banner条
 *
 * @author a_liYa
 * @date 2016-4-18 下午4:57:29
 */
public class BannerView extends RelativeLayout {

    private ViewPager mViewPager;
    private Set<OnPageChangeListener> mOnPageChangeListeners = new HashSet<>();

    private int mAutoMs = 3000; // 轮播间隔时间
    private boolean autoCarousel = true; // 是否自动轮播
    private boolean isAttached = false; // this view is currently attached to a window.

    private int mItemCount; // banner条目个数

    private BannerPagerAdapter mAdapter;

    /**
     * 宽高的比率
     */
    private float ratio_w_h = -1;
    private static final String DIVIDER_TAG = ":";

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

    // 获当前选中item的index
    public int getCurrentItem() {
        return resolvePosition(mViewPager.getAdapter().getCount(), mViewPager.getCurrentItem());
    }

    // 设置对应下标item的当前条目
    public void setCurrentItem(int item) {
        if (item < 0 || item >= mItemCount) return;

        mViewPager.setCurrentItem(item + 1);
    }

    /**
     * 初始化ViewPager
     */
    private void initView(Context context, AttributeSet attrs) {
        mViewPager = new ViewPager(context);
        addView(mViewPager, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        if (attrs == null) return;

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.Banner);
        String w_h = ta.getString(R.styleable.Banner_banner_w2h);
        if (!TextUtils.isEmpty(w_h) && w_h.contains(DIVIDER_TAG)) {
            String[] split = w_h.trim().split(DIVIDER_TAG);
            if (split != null && split.length == 2) {
                try {
                    ratio_w_h = Float.parseFloat(split[0].trim())
                            / Float.parseFloat(split[1].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        mAutoMs = ta.getInteger(R.styleable.Banner_banner_autoMs, mAutoMs);
        autoCarousel = ta.getBoolean(R.styleable.Banner_banner_isAuto, autoCarousel);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio_w_h > 0) {
            int wMode = MeasureSpec.getMode(widthMeasureSpec);
            int hMode = MeasureSpec.getMode(heightMeasureSpec);

            int wSize = MeasureSpec.getSize(widthMeasureSpec);
            int hSize = MeasureSpec.getSize(heightMeasureSpec);

            if (wMode == MeasureSpec.EXACTLY && hMode != MeasureSpec.EXACTLY) {
                heightMeasureSpec = MeasureSpec
                        .makeMeasureSpec(Math.round(wSize / ratio_w_h), MeasureSpec.EXACTLY);
            } else if (wMode != MeasureSpec.EXACTLY && hMode == MeasureSpec.EXACTLY) {
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
            mAdapter.setOnItemClickListener(null);
        }

        mAdapter = adapter;

        mViewPager.setAdapter(mAdapter);
        mItemCount = mAdapter.getTruthCount();
        mAdapter.setOnItemClickListener(mInnerOnItemClickListener);

        mViewPager.removeOnPageChangeListener(mOnPageChangeListener);
        stopAuto();

        if (mItemCount > 1) {
            mViewPager.addOnPageChangeListener(mOnPageChangeListener);
            mViewPager.setCurrentItem(1);
            startAuto();
        }
        if (mAdapterChangeListener != null) {
            mAdapterChangeListener.onAdapterChange();
        }

    }

    private OnItemClickListener mInnerOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View item, int position) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClick(item, position);
        }
    };

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        // 某一个item被选中
        @Override
        public void onPageSelected(int position) {
            if (mOnPageChangeListeners != null
                    && mOnPageChangeListeners.size() > 0) {
                position = resolvePosition(mViewPager.getAdapter().getCount(), position);
                for (OnPageChangeListener iterable : mOnPageChangeListeners) {
                    iterable.onPageSelected(position);
                }
            }
        }

        // 当滑动进行中
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            int index = resolvePosition(mViewPager.getAdapter().getCount(), position);
            if (mOnPageChangeListeners != null && mOnPageChangeListeners.size() > 0) {
                for (OnPageChangeListener iterable : mOnPageChangeListeners) {
                    iterable.onPageScrolled(index, positionOffset, positionOffsetPixels);
                }
            }
            if (positionOffset == 0.0f && positionOffsetPixels == 0) {
                if (index + 1 != position) {
                    mViewPager.setCurrentItem(index + 1, false); // false:不显示跳转过程的动画
                }
            }

        }

        // 当滑动的状态变化时
        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListeners != null
                    && mOnPageChangeListeners.size() > 0) {
                for (OnPageChangeListener iterable : mOnPageChangeListeners) {
                    iterable.onPageScrollStateChanged(state);
                }
            }

            // 当手指触摸时说明在滑动状态为SCROLL_STATE_DRAGGING 取消定时器
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                startAuto();
            } else {
                stopAuto();
            }
        }
    };

    /**
     * 获取正确的 position
     */
    static final int resolvePosition(final int count, int position) {
        if (count > 1) {
            if (position == 0) { // 首位（0） 跳转到末尾倒数第二位
                position = count - 3;
            } else if (position == count - 1) { // 末位(count)，跳转到首位（1）
                position = 0;
            } else {
                position -= 1;
            }
        }
        return position;
    }

    public BannerPagerAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置是否自动轮播
     *
     * @param canAuto true；自动轮播；false：不能自动
     */
    public void setAutoCarousel(boolean canAuto) {
        this.autoCarousel = canAuto;
        if (!canAuto) {
            stopAuto();
        }
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
     * 开始轮播
     * 建议：在Activity或Fragment的onStart()方法里调用
     */
    public final void startAuto() {
        if (isCurrCanAuto()) {
            postDelayed(mAutoRunnable, mAutoMs);
        }
    }

    /**
     * 停止轮播
     * 建议：在Activity或Fragment的onStop()方法里调用
     */
    public final void stopAuto() {
        removeCallbacks(mAutoRunnable);
    }


    // 当前是否可以自动轮播
    private boolean isCurrCanAuto() {
        return mViewPager != null
                && autoCarousel
                && isAttached
                && mAdapter != null
                && mItemCount > 1;
    }

    private Runnable mAutoRunnable = new Runnable() {
        @Override
        public void run() {
            if (mViewPager == null)
                return;
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            if (isCurrCanAuto()) {
                postDelayed(mAutoRunnable, mAutoMs);
            }
        }
    };

    private OnItemClickListener mOnItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private OnAdapterChangeListener mAdapterChangeListener;

    void setAdapterChangeListener(OnAdapterChangeListener listener) {
        mAdapterChangeListener = listener;
    }

}
