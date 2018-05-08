# BannerView

#### 依赖
```
compile 'com.aliya:bannerview:2.0.0'
compile 'com.android.support:support-v4:x.x.x' // 因为ViewPager属于v4包

```

#### 使用

> 1. 布局

```
<com.aliya.view.banner.BannerView
        android:id="@+id/banner_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:banner_autoMs="3000"
        app:banner_isAuto="true"
        app:banner_w2h="360:206" />

```

> 2. 设置Adapter
```
BannerView banner = findViewById(R.id.banner_view);

BannerPagerAdapter adapter = new BannerPagerAdapter() {

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

```

> 3. 设置索引指示器  

 `布局`

```xml
    <com.aliya.view.banner.BannerView
        android:id="@+id/banner_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/colorPrimary"
        app:banner_autoMs="3000"
        app:banner_isAuto="false"
        app:banner_w2h="360:206">

        <com.aliya.view.banner.BannerIndicatorLayout
            android:id="@+id/banner_indicator"
            android:layout_width="wrap_content"
            android:layout_height="35dp"
            android:layout_alignParentBottom="true"
            android:layout_alignParentRight="true"
            android:layout_marginRight="15dp"
            app:banner_itemMargin="6dp"
            app:banner_onlyOneVisible="true" />

    </com.aliya.view.banner.BannerView>
```

 `代码`  
```java
indicator = findViewById(R.id.banner_indicator);
        
indicator.setAdapter(new BannerIndicatorLayout.IndicatorAdapter() {
        
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            return LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_banner_indicator_dot, parent);
        }
        return convertView;
    }

    @Override
    public void onPagerScrolled(int index, View indexView, float indexOffset, int
                    laterIndex, View laterIndexView, float laterIndexOffset) {

    }
            
});

indicator.setupWithBanner(banner);

```

