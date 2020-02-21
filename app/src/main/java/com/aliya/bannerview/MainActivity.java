package com.aliya.bannerview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliya.view.banner.BannerIndicatorLayout;
import com.aliya.view.banner.BannerPagerAdapter;
import com.aliya.view.banner.BannerView;
import com.aliya.view.banner.OnItemClickListener;

import static com.aliya.bannerview.Utils.inflate;

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
        findViewById(R.id.btn_gallery).setOnClickListener(this);
        findViewById(R.id.open_surge).setOnClickListener(this);

        adapter = new BannerPagerAdapter() {
            @Override
            public int getTruthCount() {
                return 5;
            }

            @Override
            protected View getItem(ViewGroup container, int position) {
                View view = inflate(R.layout.item_banner_view_test, container, false);
                TextView tv = view.findViewById(R.id.tv_content);
                tv.setText("index " + position);
                return view;
            }

        };
        adapter.setOnItemClickListener(this);

        banner.setAdapter(adapter);
        banner.getViewPager().setTransitionAnimationScale(4); // 新增控制过渡动画比例的方法

        indicator.setAdapter(new BannerIndicatorLayout.IndicatorAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    return inflate(R.layout.item_banner_indicator_dot, parent, false);
                }
                return convertView;
            }

            @Override
            public void onPagerScrolled(int index, View indexView, float indexOffset, int
                    laterIndex, View laterIndexView, float laterIndexOffset) {
                int count = indicator.getChildCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        View childAt = indicator.getChildAt(i);
                        if (childAt != null && childAt != laterIndexView && childAt != indexView) {
                            childAt.setScaleX(1);
                            childAt.setScaleY(1);
                        }
                    }
                }
                if (laterIndexView != null) {
                    laterIndexView.setScaleX(1 + 2f / 3 * (indexOffset));
                    laterIndexView.setScaleY(1 + 2f / 3 * (indexOffset));
                }
                if (indexView != null) {
                    indexView.setScaleX(5f / 3 - 2f / 3 * (indexOffset));
                    indexView.setScaleY(5f / 3 - 2f / 3 * (indexOffset));
                }
            }
        });

        indicator.setupWithBanner(banner);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                banner.setAuto(true);
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
                        View view = inflate(R.layout.item_banner_view_test, container, false);
                        TextView tv = view.findViewById(R.id.tv_content);
                        tv.setText("index " + position);
                        return view;
                    }

                };
                banner.setAdapter(adapter);
                break;
            case R.id.btn_1_num:
                adapter = new BannerPagerAdapter(false) {

                    @Override
                    public int getTruthCount() {
                        return 1;
                    }

                    @Override
                    protected View getItem(ViewGroup container, int position) {
                        View view = inflate(R.layout.item_banner_view_test, container, false);
                        TextView tv = view.findViewById(R.id.tv_content);
                        tv.setText("index " + position);
                        return view;
                    }

                };
                banner.setAdapter(adapter);
                banner.setAuto(false);
                break;
            case R.id.btn_5_num:
                adapter = new BannerPagerAdapter() {

                    @Override
                    public int getTruthCount() {
                        return 5;
                    }

                    @Override
                    protected View getItem(ViewGroup container, int position) {
                        View view = inflate(R.layout.item_banner_view_test, container, false);
                        TextView tv = view.findViewById(R.id.tv_content);
                        tv.setText("index " + position);
                        return view;
                    }

                };
                banner.setAdapter(adapter);
                banner.setAuto(true);
                break;
            case R.id.btn_gallery:
                startActivity(new Intent(this, GallerySimpleActivity.class));
                break;
            case R.id.open_surge:
                startActivity(new Intent(this, SurgeSimpleActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(View item, int position) {
        Log.e("TAG", "position " + position);
    }
}
