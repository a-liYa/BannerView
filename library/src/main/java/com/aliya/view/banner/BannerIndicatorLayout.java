package com.aliya.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aliya.view.banner.view.BannerViewPager;

import java.util.LinkedList;
import java.util.Queue;

/**
 * BannerView的指示器Layout
 *
 * @author a_liYa
 * @date 2016-4-23 上午10:49:08
 */
public class BannerIndicatorLayout extends LinearLayout implements OnAdapterChangeListener {

    private IndicatorAdapter mAdapter;
    private BannerView mBannerView;

    private int mItemMargin;
    private boolean onlyOneVisible = true;

    /**
     * 子条目View集合
     */
    private SparseArray<View> mAttachItemViews = new SparseArray<>();
    /**
     * 缓存Views
     */
    private Queue<View> mCacheItemViews = new LinkedList<>();

    private int mChildCount = 0;
    /**
     * 被选中的item对应的Key
     */
    private Integer selectedKey = SELECTED_NO_KEY;

    private static final int SELECTED_NO_KEY = -1;

    public BannerIndicatorLayout(Context context, AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public BannerIndicatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BannerIndicatorLayout(Context context) {
        this(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicatorLayout);
            mItemMargin = ta.getDimensionPixelSize(R.styleable.BannerIndicatorLayout_banner_itemMargin,
                    0);
            onlyOneVisible = ta.getBoolean(R.styleable.BannerIndicatorLayout_banner_onlyOneVisible, true);
            ta.recycle();
        }

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    /**
     * 设置适配器
     *
     * @param adapter Adapter
     */
    public void setAdapter(IndicatorAdapter adapter) {
        this.mAdapter = adapter;
        selectedKey = SELECTED_NO_KEY;
        initChild();
    }

    /**
     * 设置BannerView（即与BannerView绑定）
     *
     * @param bannerView BannerView
     */
    public void setupWithBanner(BannerView bannerView) {

        if (mBannerView != null)
            return;

        this.mBannerView = bannerView;
        mBannerView.setAdapterChangeListener(this);

        // 关联Banner条目与指示器
        mBannerView.addOnPageChangeListener(mOnPageChangeListener);

        initChild();

        if (mBannerView.getAdapter() != null && mBannerView.getAdapter().getCount() > 0) {
            // 首次没有onPageSelected()回调
            mOnPageChangeListener.onPageSelected(mBannerView.getCurrentItem());
        }

    }

    /**
     * 初始化Child
     */
    private void initChild() {
        if (null != mBannerView && null != mAdapter) {
            if (mBannerView.getAdapter() != null) {
                mChildCount = mBannerView.getAdapter().getTruthCount();

                setVisibility(!onlyOneVisible && mChildCount < 2 ? INVISIBLE : VISIBLE);

                removeAllViews();
                for (int i = 0; i < mAttachItemViews.size(); i++) {
                    mCacheItemViews.add(mAttachItemViews.valueAt(i));
                }
                mAttachItemViews.clear();
                for (int i = 0; i < mChildCount; i++) {
                    View poll = mCacheItemViews.poll();
                    if (poll != null) poll.setSelected(false);
                    final View child = mAdapter.getView(i, poll, this);
                    mAttachItemViews.put(i, child);
                    LayoutParams lp = (LayoutParams) child
                            .getLayoutParams();
                    if (lp == null) {
                        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup
                                .LayoutParams.WRAP_CONTENT);
                    }
                    int left = Math.round(mItemMargin / 2.0f);
                    int right = Math.round(mItemMargin / 2.0f);
                    if (i == 0) {
                        left = 0;
                    }
                    if (i == mChildCount - 1) {
                        right = 0;
                    }
                    lp.setMargins(left, 0, right, 0);

                    addView(child, i, lp);
                }
                mCacheItemViews.clear();
            }
        }
    }

    private BannerViewPager.OnPageChangeListener mOnPageChangeListener = new BannerViewPager
            .OnPageChangeListener() {

        @Override
        public void onPageScrolled(int screenFirstVisiblePosition,
                                   float positionOffset, int positionOffsetPixels) {

            if (mAdapter != null) {
                View indexView = mAttachItemViews.get(screenFirstVisiblePosition);

                int laterIndex = (screenFirstVisiblePosition + 1) % mChildCount;
                View laterIndexView = mAttachItemViews.get(laterIndex);
                float laterIndexOffset = 1.0f - positionOffset;

                if (positionOffset == 0) {
                    int prevIndex = (screenFirstVisiblePosition + mChildCount - 1) % mChildCount;
                    mAdapter.onPagerScrolled(screenFirstVisiblePosition, indexView, positionOffset,
                            prevIndex, mAttachItemViews.get(prevIndex), laterIndexOffset);
                }

                mAdapter.onPagerScrolled(screenFirstVisiblePosition, indexView, positionOffset,
                        laterIndex, laterIndexView, laterIndexOffset);
            }

        }

        @Override
        public void onPageSelected(int position) {
            if (selectedKey != position) {
                // 设置当前item为选中状态
                View selectedView = mAttachItemViews.get(position);
                if (selectedView != null) {
                    selectedView.setSelected(true);
                }

                // 取消上个item的选中状态
                View lastView = mAttachItemViews.get(selectedKey);
                if (lastView != null) {
                    lastView.setSelected(false);
                }

            }
            selectedKey = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public void onAdapterChange() {
        selectedKey = SELECTED_NO_KEY;
        initChild();
        if (mBannerView.getAdapter() != null && mBannerView.getAdapter().getCount() > 0) {
            // 首次没有onPageSelected()回调
            mOnPageChangeListener.onPageSelected(mBannerView.getCurrentItem());
        }
    }

    /**
     * BannerIndicator适配器接口
     *
     * @author a_liYa
     * @date 2016-4-23 上午10:58:36
     */
    public interface IndicatorAdapter {

        View getView(int position, View convertView, ViewGroup parent);

        /**
         * 页面滚动回调
         *
         * @param index            当前屏幕可见第一个item下标
         * @param indexView        第一个item
         * @param indexOffset      第一个滚动比例 [0.0 - 1.0) : 选中 - 未选中
         * @param laterIndex       当前屏幕可见第二个item下标
         * @param laterIndexView   第二个item
         * @param laterIndexOffset 第二个滚动比例 [1.0 - 0.0) : 未选中 - 选中
         *                         总结：0.0 代表选中 - 1.0 代表未选中
         */
        void onPagerScrolled(int index, View indexView,
                             float indexOffset, int laterIndex, View laterIndexView,
                             float laterIndexOffset);
    }

}
