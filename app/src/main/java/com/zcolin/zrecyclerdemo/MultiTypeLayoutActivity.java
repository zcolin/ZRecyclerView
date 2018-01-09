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
import android.widget.Toast;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;
import com.zcolin.zrecyclerdemo.adapter.ZRecyclerMultiTypeAdapter;

import java.util.ArrayList;

public class MultiTypeLayoutActivity extends AppCompatActivity {

    private ZRecyclerView             recyclerView;
    private ZRecyclerMultiTypeAdapter recyclerAdapter;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setGridLayout(false, 3);//默认已设置LinearLayoutManager
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());
        recyclerView.setEmptyView(this, R.layout.view_recycler_empty);
        recyclerView.addHeaderView(this, R.layout.view_recyclerheader);

        recyclerView.setOnItemClickListener((BaseRecyclerAdapter.OnItemClickListener<String>) (covertView, position, data) -> Toast.makeText
                (MultiTypeLayoutActivity.this, data, Toast.LENGTH_SHORT)
                                                                                                                                   .show());

        notifyData(new ArrayList<>(), false);
        recyclerView.refreshWithPull();
    }

    /**
     * 设置数据Adapter
     */
    public void notifyData(ArrayList<String> list, boolean isClear) {
        if (recyclerAdapter == null) {
            recyclerAdapter = new ZRecyclerMultiTypeAdapter();
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
        new Handler().postDelayed(() -> {
            notifyData(setList(page), page == 1);
            recyclerView.setPullLoadMoreCompleted();
            if (page == 2) {
                recyclerView.setNoMore(true);
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
            recyclerView.setNoMore(false);
        }

        @Override
        public void onLoadMore() {
            mPage = mPage + 1;
            requestData(mPage);
        }
    }
}
