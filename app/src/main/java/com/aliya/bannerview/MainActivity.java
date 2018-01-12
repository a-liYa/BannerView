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

    BannerIndicatorLayout indicator;
    BannerView banner;

    private BannerPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indicator = findViewById(R.id.banner_indicator);
        banner = findViewById(R.id.banner_view);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_0_num).setOnClickListener(this);
        findViewById(R.id.btn_1_num).setOnClickListener(this);
        findViewById(R.id.btn_5_num).setOnClickListener(this);

        adapter = new BannerPagerAdapter() {

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

        banner.setAdapter(adapter);

        banner.setOnItemClickListener(this);

        indicator.setAdapter(new BannerIndicatorLayout.IndicatorAdapter() {
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

        indicator.setupWithBanner(banner);

    }

    private View inflate(int resId, ViewGroup root) {
        return LayoutInflater.from(this).inflate(resId, root, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                banner.setAutoCarousel(true);
                banner.startAuto();
                break;
            case R.id.btn_stop:
                banner.stopAuto();
                break;
            case R.id.btn_0_num:
                adapter = new BannerPagerAdapter() {

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
                banner.setAdapter(adapter);
                break;
            case R.id.btn_1_num:
                adapter = new BannerPagerAdapter() {

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
                banner.setAdapter(adapter);
                banner.setAutoCarousel(false);
                break;
            case R.id.btn_5_num:
                adapter = new BannerPagerAdapter() {

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
                banner.setAdapter(adapter);
                banner.setAutoCarousel(true);
                break;
        }
    }

    @Override
    public void onItemClick(View item, int position) {
        Log.e("TAG", "position " + position);
    }
}
