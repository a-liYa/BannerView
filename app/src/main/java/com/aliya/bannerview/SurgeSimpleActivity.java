package com.aliya.bannerview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliya.bannerview.widget.XBannerView;
import com.aliya.view.banner.BannerPagerAdapter;

import static com.aliya.bannerview.Utils.inflate;

/**
 * 澎湃新闻 BannerView 示例
 *
 * @author a_liYa
 * @date 2020-02-21 16:32.
 */
public class SurgeSimpleActivity extends Activity {

    XBannerView mBannerView;
    BannerPagerAdapter adapter;
    LinearLayout mIndicatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surge_simple);

        mBannerView = findViewById(R.id.banner_view);
        mIndicatorLayout = findViewById(R.id.indicator_layout);

        adapter = new BannerPagerAdapter() {
            @Override
            public int getTruthCount() {
                return 4;
            }

            @Override
            protected View getItem(ViewGroup container, int position) {
                View view = inflate(R.layout.item_banner_view_test, container, false);
                TextView tv = view.findViewById(R.id.tv_content);
                tv.setText("index " + position);
                return view;
            }

        };
        mBannerView.setAdapter(adapter);

        mBannerView.setCallback(new XBannerView.Callback() {

            private ValueAnimator mAnimator;

            @Override
            public void onPageSelected(int position) {
                int childCount = mIndicatorLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    ProgressBar progressBar = (ProgressBar) mIndicatorLayout.getChildAt(i);
                    if (i <= position) {
                        progressBar.setProgress(progressBar.getMax());
                    } else {
                        progressBar.setProgress(0);
                    }
                }
            }

            @Override
            public void onPostDelayed(int selectedPosition, int delayMs) {
                if (selectedPosition < adapter.getTruthCount() - 1) {
                    final ProgressBar progressBar = (ProgressBar)
                            mIndicatorLayout.getChildAt(selectedPosition + 1);
                    mAnimator = ValueAnimator.ofInt(0, progressBar.getMax());
                    mAnimator.setDuration(delayMs);
                    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            progressBar.setProgress((int) animation.getAnimatedValue());
                        }
                    });
                    mAnimator.start();
                }
            }

            @Override
            public void onRemoveDelayed(int selectedPosition) {
                if (mAnimator != null) {
                    mAnimator.cancel();
                    mAnimator = null;
                }
                if (selectedPosition < adapter.getTruthCount() - 1) {
                    final ProgressBar progressBar = (ProgressBar)
                            mIndicatorLayout.getChildAt(selectedPosition + 1);
                    progressBar.setProgress(0);
                }
            }
        });
    }
}
