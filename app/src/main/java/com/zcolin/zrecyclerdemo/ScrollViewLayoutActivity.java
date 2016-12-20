/*
 * *********************************************************
 *   author   colin
 *   company  fosung
 *   email    wanglin2046@126.com
 *   date     16-12-16 下午4:52
 * ********************************************************
 */

package com.zcolin.zrecyclerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.zcolin.gui.zrecyclerview.swiperefreshlayout.SwipeRefreshLayout;
import com.zcolin.gui.zrecyclerview.swiperefreshlayout.ZSwipeRefreshLayout;

public class ScrollViewLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);


        final ZSwipeRefreshLayout refreshLayout = (ZSwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }
}
