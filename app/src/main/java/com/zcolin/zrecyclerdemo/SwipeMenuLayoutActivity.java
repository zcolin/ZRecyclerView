/*
 * *********************************************************
 *   author   colin
 *   company  fosung
 *   email    wanglin2046@126.com
 *   date     16-12-19 下午5:22
 * ********************************************************
 */

package com.zcolin.zrecyclerdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecycleViewDivider;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;
import com.zcolin.gui.zrecyclerview.ZSwipeMenuRecyclerView;
import com.zcolin.zrecyclerdemo.adapter.SwipeMenuRecyclerAdapter;

import java.util.ArrayList;

public class SwipeMenuLayoutActivity extends AppCompatActivity {

    private ZSwipeMenuRecyclerView   recyclerView;
    private SwipeMenuRecyclerAdapter recyclerAdapter;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipemenu);

        recyclerView = (ZSwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());
        recyclerView.addItemDecoration(new ZRecycleViewDivider(this, LinearLayout.HORIZONTAL, 1, Color.GREEN));
        recyclerView.getLoadMoreFooterView().setBackgroundColor(Color.BLUE);
        recyclerView.setLoadMoreTextColor(Color.WHITE);
        recyclerView.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(View covertView, int position, String data) {
                Toast.makeText(SwipeMenuLayoutActivity.this, data, Toast.LENGTH_SHORT)
                     .show();

            }
        });

        recyclerView.refreshWithPull();
    }

    /**
     * 设置数据Adapter
     */
    public void notifyData(ArrayList<String> list, boolean isClear) {
        if (recyclerAdapter == null) {
            recyclerAdapter = new SwipeMenuRecyclerAdapter();
            recyclerAdapter.addDatas(list);
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            if (isClear) {
                recyclerAdapter.setDatas(list);
            } else {
                recyclerAdapter.addDatas(list);
            }
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 模仿从网络请求数据
     */
    public void requestData(final int page) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyData(setList(page), page == 1);
                recyclerView.setPullLoadMoreCompleted();
                if (page == 2) {
                    recyclerView.setNoMore(true);
                }
            }
        }, 1000);
    }

    //制造假数据
    private ArrayList<String> setList(int page) {
        ArrayList<String> dataList = new ArrayList<>();
        int start = 15 * (page - 1);
        for (int i = start; i < 15 * page; i++) {
            dataList.add(String.format("第%d条数据", i));
        }
        return dataList;
    }

    class PullLoadMoreListener implements ZRecyclerView.PullLoadMoreListener {
        @Override
        public void onRefresh() {
            mPage = 1;
            requestData(mPage);
        }

        @Override
        public void onLoadMore() {
            mPage = mPage + 1;
            requestData(mPage);
        }
    }
}
