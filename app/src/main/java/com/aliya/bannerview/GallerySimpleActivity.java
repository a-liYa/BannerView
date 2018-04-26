package com.aliya.bannerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliya.view.banner.BannerPagerAdapter;
import com.aliya.view.banner.BannerView;
import com.aliya.view.banner.magic.GalleryPageTransformer;

public class GallerySimpleActivity extends Activity {

    BannerView banner;
    private BannerPagerAdapter adapter;

    int[] imgIds = {R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e, R.mipmap.f, R
            .mipmap.g, R.mipmap.h, R.mipmap.i};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_simple);

        banner = findViewById(R.id.banner_view);

        adapter = new BannerPagerAdapter() {

            @Override
            public int getTruthCount() {
                return 8;
            }

            @Override
            protected View getItem(ViewGroup container, int position) {
                View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout
                        .module_pager_item, container, false);
                TextView textView = inflate.findViewById(R.id.image_view);
                textView.setText(String.valueOf(position));
                inflate.setTag(position);
                return inflate;
            }

        };

        ViewPager viewPager = banner.getViewPager();

        viewPager.setPageTransformer(true,
                new GalleryPageTransformer(viewPager, dip2px(20), 1, 0.86f));
        banner.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        banner.startAuto();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        banner.stopAuto();
    }

    /**
     * dip转换px
     */
    public int dip2px(float dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

}
