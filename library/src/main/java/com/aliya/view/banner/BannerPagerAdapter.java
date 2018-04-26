package com.aliya.view.banner;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.SoftReference;

/**
 * Banner的PagerAdapter的封装
 *
 * @author a_liYa
 * @date 2016-4-18 下午1:38:42
 */
public abstract class BannerPagerAdapter extends PagerAdapter {

    private SparseArray<SoftReference<View>> mItemCaches = new SparseArray<>();

    public abstract int getTruthCount();

    /**
     * @param container 父容器
     * @param position  item index
     * @return view
     * @see #instantiateItem(ViewGroup, int)
     */
    protected abstract View getItem(ViewGroup container, int position);

    @Override
    public final int getCount() {
        final int truthCount = getTruthCount();
        return truthCount < 2 ? truthCount : truthCount + 2;
    }

    @Override
    public final Object instantiateItem(ViewGroup container, final int position) {

        int index = BannerView.resolvePosition(getCount(), position);

        View item;
        SoftReference<View> softReference = mItemCaches.get(position);

        if (softReference == null || (item = softReference.get()) == null) {
            item = getItem(container, index % getTruthCount());
            mItemCaches.put(position, new SoftReference<>(item));
        }

        container.addView(item);
        item.setOnClickListener(mOnClickListener);
        item.setTag(R.id.tag_position, index);
        return item;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                Object tag = v.getTag(R.id.tag_position);
                if (tag instanceof Integer) {
                    onItemClickListener.onItemClick(v, (Integer) tag);
                }
            }
        }
    };

    private OnItemClickListener onItemClickListener;

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}