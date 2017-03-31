/*
 * *********************************************************
 *   author   colin
 *   company  fosung
 *   email    wanglin2046@126.com
 *   date     17-3-31 上午10:02
 * ********************************************************
 */

package com.zcolin.zrecyclerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;
import com.zcolin.zrecyclerdemo.adapter.ZRecyclerMultiTypeAdapter;

import java.util.ArrayList;


/**
 * MD风格的RecyclerView，多个Item的列表示例
 */
public class DesignSupportActivity extends AppCompatActivity {

    private ZRecyclerView             recyclerView;
    private ZRecyclerMultiTypeAdapter mRecyclerViewAdapter;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designsupport);
        init();
    }

    private void init() {
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "FloatActionBar-click", Snackbar.LENGTH_LONG)
                        .setAction("toast", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
            }
        });


        recyclerView = (ZRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        //设置HeaderView
        recyclerView.setHeaderView(this, R.layout.view_recyclerheader);

        //下拉和到底加载的进度条样式，默认为 ProgressStyle.BallSpinFadeLoaderIndicator
        recyclerView.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<String>() {
            @Override
            public void onItemClick(View covertView, int position, String data) {
                Toast.makeText(DesignSupportActivity.this, data, Toast.LENGTH_SHORT)
                     .show();
            }
        });

        //绑定Adapter
        notifyData(new ArrayList<String>(), false);

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyData(setList(page), page == 1);
                        recyclerView.setPullLoadMoreCompleted();
                    }
                });
            }
        }, 1000);
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
