package com.aliya.view.banner;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.aliya.view.banner.view.PagerAdapter;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Banner的PagerAdapter的封装
 *
 * @author a_liYa
 * @date 2016-4-18 下午1:38:42
 */
public abstract class BannerPagerAdapter extends PagerAdapter {

    private SparseArray<Queue<SoftReference<View>>> mItemCaches = new SparseArray<>();

    private boolean canCycle = true; // 默认可以循环

    public BannerPagerAdapter() {
    }

    public BannerPagerAdapter(boolean canCycle) {
        this.canCycle = canCycle;
    }

    public abstract int getTruthCount();

    /**
     * 创建 item view
     *
     * @param container 父容器
     * @param position  item index
     * @return view
     * @see #instantiateItem(ViewGroup, int)
     */
    protected abstract View getItem(ViewGroup container, int position);

    public boolean isCanCycle() {
        return canCycle;
    }

    @Override
    public final int getCount() {
        if (canCycle && getTruthCount() > 0) {
            return Integer.MAX_VALUE;
        }
        return getTruthCount();
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        int index = position % getTruthCount();

        View item = null;
        Queue<SoftReference<View>> itemQueue = mItemCaches.get(index);
        if (itemQueue == null) {
            mItemCaches.put(index, itemQueue = new LinkedList<>());
        }
        SoftReference<View> softItem = itemQueue.poll();
        while (softItem != null) {
            if ((item = softItem.get()) != null) {
                break;
            }
            softItem = itemQueue.poll();
        }

        if (item == null) {
            item = getItem(container, index);
        }

        container.addView(item);
        item.setOnClickListener(mOnClickListener);
        item.setTag(R.id.tag_position, index);
        return item;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        int index = position % getTruthCount();
        Queue<SoftReference<View>> itemQueue = mItemCaches.get(index);
        if (itemQueue == null) {
            mItemCaches.put(index, itemQueue = new LinkedList<>());
        }
        itemQueue.offer(new SoftReference<>((View)object));
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

}