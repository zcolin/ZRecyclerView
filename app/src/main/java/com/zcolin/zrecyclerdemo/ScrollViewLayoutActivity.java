/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-1-9 下午2:46
 * ********************************************************
 */

package com.zcolin.zrecyclerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.zcolin.gui.zrecyclerview.swiperefreshlayout.ZSwipeRefreshLayout;

public class ScrollViewLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);


        final ZSwipeRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> refreshLayout.setRefreshing(false), 1000));
    }
}
