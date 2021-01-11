package com.zcolin.zrecyclerdemo;

import android.os.Bundle;
import android.os.Handler;

import com.zcolin.gui.zrecyclerview.ZRecyclerView;
import com.zcolin.zrecyclerdemo.adapter.ZRecyclerAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class DecorationActivity extends AppCompatActivity {

    private ZRecyclerView    recyclerView;
    private ZRecyclerAdapter recyclerAdapter;
    private int              mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addDefaultItemDecoration();
        recyclerView.addHeaderView(this, R.layout.view_recyclerheader);
        recyclerView.addFooterView(this, R.layout.view_recyclerfooter);
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        notifyData(new ArrayList<>(), false);
        recyclerView.refreshWithPull();     //有下拉效果的数据刷新
    }

    /**
     * 设置数据Adapter
     */
    public void notifyData(ArrayList<String> list, boolean isClear) {
        if (recyclerAdapter == null) {
            recyclerAdapter = new ZRecyclerAdapter(ZRecyclerAdapter.TYPE_LISTVIEW);
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
