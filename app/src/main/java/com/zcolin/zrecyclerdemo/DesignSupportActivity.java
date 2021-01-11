package com.zcolin.zrecyclerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;
import com.zcolin.zrecyclerdemo.adapter.ZRecyclerMultiTypeAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;


/**
 * MD风格的RecyclerView，多个Item的列表示例
 */
public class DesignSupportActivity extends AppCompatActivity {

    private ZRecyclerView             recyclerView;
    private ZRecyclerMultiTypeAdapter mRecyclerViewAdapter;
    private int                       mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designsupport);
        init();
    }

    private void init() {
        findViewById(R.id.fab).setOnClickListener(v -> Snackbar.make(v, "FloatActionBar-click", Snackbar.LENGTH_LONG)
                                                               .setAction("toast", v1 -> {

                                                               }));


        recyclerView = findViewById(R.id.pullLoadMoreRecyclerView);
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        //设置HeaderView
        recyclerView.addHeaderView(this, R.layout.view_recyclerheader);

        //下拉和到底加载的进度条样式，默认为 ProgressStyle.BallSpinFadeLoaderIndicator
        recyclerView.setOnItemClickListener((BaseRecyclerAdapter.OnItemClickListener<String>) (covertView, position,
                data) -> Toast
                .makeText(DesignSupportActivity.this, data, Toast.LENGTH_SHORT)
                .show());

        //绑定Adapter
        notifyData(new ArrayList<>(), false);

        recyclerView.refreshWithPull();     //有下拉效果的数据刷新
    }


    public void notifyData(ArrayList<String> list, boolean isClear) {
        if (mRecyclerViewAdapter == null) {
            mRecyclerViewAdapter = new ZRecyclerMultiTypeAdapter();
            mRecyclerViewAdapter.addDatas(list);
            recyclerView.setAdapter(mRecyclerViewAdapter);
        } else {
            if (isClear) {
                mRecyclerViewAdapter.setDatas(list);
            } else {
                mRecyclerViewAdapter.addDatas(list);
            }
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    public void getDataFromShopList(final int page) {
        new Handler().postDelayed(() -> runOnUiThread(() -> {
            notifyData(setList(page), page == 1);
            recyclerView.setPullLoadMoreCompleted();
        }), 1000);
    }

    //制造假数据
    private ArrayList<String> setList(int page) {
        ArrayList<String> dataList = new ArrayList<>();
        int start = 20 * (page - 1);
        for (int i = start; i < 20 * page; i++) {
            dataList.add("Frist" + i);
        }
        return dataList;
    }

    class PullLoadMoreListener implements ZRecyclerView.PullLoadMoreListener {
        @Override
        public void onRefresh() {
            mPage = 1;
            getDataFromShopList(mPage);
        }

        @Override
        public void onLoadMore() {
            mPage = mPage + 1;
            getDataFromShopList(mPage);
        }
    }
}
