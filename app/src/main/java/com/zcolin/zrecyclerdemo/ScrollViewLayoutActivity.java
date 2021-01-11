package com.zcolin.zrecyclerdemo;

import android.os.Bundle;
import android.os.Handler;

import com.zcolin.gui.zrecyclerview.swiperefreshlayout.ZSwipeRefreshLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ScrollViewLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);


        final ZSwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> refreshLayout.setRefreshing(false),
                                                                           1000));
    }
}
