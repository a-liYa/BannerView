package com.aliya.bannerview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliya.view.banner.BannerIndicatorLayout;
import com.aliya.view.banner.BannerPagerAdapter;
import com.aliya.view.banner.BannerView;
import com.aliya.view.banner.OnItemClickListener;

public class MainActivity extends Activity implements View.OnClickListener, OnItemClickListener {

    BannerIndicatorLayout mBannerIndicator;
    BannerView mBannerView;

    private BannerPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBannerIndicator = findViewById(R.id.banner_indicator);
        mBannerView = findViewById(R.id.banner_view);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_0_num).setOnClickListener(this);
        findViewById(R.id.btn_1_num).setOnClickListener(this);
        findViewById(R.id.btn_5_num).setOnClickListener(this);

        mPagerAdapter = new BannerPagerAdapter() {

            @Override
            public int getTruthCount() {
                return 3;
            }

            @Override
            protected View getItem(ViewGroup container, int position) {
                View view = inflate(R.layout.item_banner_view_test, container);
                TextView tv = view.findViewById(R.id.tv_content);
                tv.setText("index " + position);
                return view;
            }

        };

        mBannerView.setAdapter(mPagerAdapter);

        mBannerView.setOnItemClickListener(this);

        mBannerIndicator.setAdapter(new BannerIndicatorLayout.IndicatorAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    return inflate(R.layout.item_banner_indicator_dot, parent);
                }
                return convertView;
            }

            @Override
            public void onPagerScrolled(int index, View indexView, float indexOffset, int
                    laterIndex, View laterIndexView, float laterIndexOffset) {

            }
        });

        mBannerIndicator.setupWithBanner(mBannerView);

    }

    private View inflate(int resId, ViewGroup root) {
        return LayoutInflater.from(this).inflate(resId, root, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                mBannerView.setAutoCarousel(true);
                mBannerView.startAuto();
                break;
            case R.id.btn_stop:
                mBannerView.stopAuto();
                break;
            case R.id.btn_0_num:
                mPagerAdapter = new BannerPagerAdapter() {

                    @Override
                    public int getTruthCount() {
                        return 0;
                    }

                    @Override
                    protected View getItem(ViewGroup container, int position) {
                        View view = inflate(R.layout.item_banner_view_test, container);
                        TextView tv = view.findViewById(R.id.tv_content);
                        tv.setText("index " + position);
                        return view;
                    }

                };
                mBannerView.setAdapter(mPagerAdapter);
                break;
            case R.id.btn_1_num:
                mPagerAdapter = new BannerPagerAdapter() {

                    @Override
                    public int getTruthCount() {
                        return 1;
                    }

                    @Override
                    protected View getItem(ViewGroup container, int position) {
                        View view = inflate(R.layout.item_banner_view_test, container);
                        TextView tv = view.findViewById(R.id.tv_content);
                        tv.setText("index " + position);
                        return view;
                    }

                };
                mBannerView.setAdapter(mPagerAdapter);
                break;
            case R.id.btn_5_num:
                mPagerAdapter = new BannerPagerAdapter() {

                    @Override
                    public int getTruthCount() {
                        return 5;
                    }

                    @Override
                    protected View getItem(ViewGroup container, int position) {
                        View view = inflate(R.layout.item_banner_view_test, container);
                        TextView tv = view.findViewById(R.id.tv_content);
                        tv.setText("index " + position);
                        return view;
                    }

                };
                mBannerView.setAdapter(mPagerAdapter);
                break;
        }
    }

    @Override
    public void onItemClick(View item, int position) {
        Log.e("TAG", "position " + position);
    }
}
