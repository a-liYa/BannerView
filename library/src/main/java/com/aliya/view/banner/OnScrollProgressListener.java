package com.aliya.view.banner;

import android.support.annotation.FloatRange;

/**
 * @author: lujialei
 * @date: 2020-02-21
 * @describe:
 */

public interface OnScrollProgressListener {
    void onUpdateProgress(int currentItem, int count,@FloatRange(from=0f,to=1f) float progress);
    void onScrolling();
}
