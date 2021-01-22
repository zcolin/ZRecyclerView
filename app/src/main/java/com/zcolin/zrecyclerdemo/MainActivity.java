package com.zcolin.zrecyclerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.gui.zrecyclerview.ZRecyclerView;
import com.zcolin.zrecyclerdemo.adapter.ZRecyclerAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ZRecyclerView    recyclerView;
    private ZRecyclerAdapter recyclerAdapter;
    private View             headerView2;
    private int              mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        //recyclerView.setGridLayout(true, 2);  //默认为LinearLayoutManager
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreListener());

        //设置数据为空时的EmptyView，DataObserver是注册在adapter之上的，也就是setAdapter是注册上，notifyDataSetChanged的时候才会生效
        recyclerView.setEmptyView(this, R.layout.view_recycler_empty);

        //设置HeaderView和footerView
        recyclerView.addHeaderView(this, R.layout.view_recyclerheader);
        headerView2 = LayoutInflater.from(this).inflate(R.layout.view_recyclerheader, null);
        ((TextView) headerView2.findViewById(R.id.textView)).setText("HEDER 2");
        recyclerView.addHeaderView(headerView2);
        recyclerView.addFooterView(this, R.layout.view_recyclerfooter);

        //recyclerView.setLoadMoreProgressView(view);
        //recyclerView.setIsShowNoMore(false);      //不显示已加载全部
        // recyclerView.setIsLoadMoreEnabled(false);//到底加载是否可用
        // recyclerView.setIsRefreshEnabled(false);//下拉刷新是否可用
        //recyclerView.setIsProceeConflict(true);   //处理与子控件的冲突，如viewpager
        //recyclerView.setLoadMoreFooter(customview implements ILoadMoreFooter);   //设置自定义的加载Footer
        //recyclerView.setLoadMoreText("正在加载...", "正在加载...", "*****已加载全部*****");//设置加载文字
        //recyclerView.addDefaultItemDecoration();//增加默认分割线
        recyclerView.setOnItemClickListener((BaseRecyclerAdapter.OnItemClickListener<String>) (covertView, position,
                data) -> {
            Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            if (position == 0) {
                Intent intent = new Intent(MainActivity.this, ScrollViewLayoutActivity.class);
                startActivity(intent);
            } else if (position == 1) {
                Intent intent = new Intent(MainActivity.this, EmptyViewLayoutActivity.class);
                startActivity(intent);
            } else if (position == 2) {
                Intent intent = new Intent(MainActivity.this, GridLayoutActivity.class);
                startActivity(intent);
            } else if (position == 3) {
                Intent intent = new Intent(MainActivity.this, StaggeredLayoutActivity.class);
                startActivity(intent);
            } else if (position == 4) {
                Intent intent = new Intent(MainActivity.this, MultiTypeLayoutActivity.class);
                startActivity(intent);
            } else if (position == 5) {
                Intent intent = new Intent(MainActivity.this, SwipeMenuLayoutActivity.class);
                startActivity(intent);
            } else if (position == 6) {
                Intent intent = new Intent(MainActivity.this, DecorationActivity.class);
                startActivity(intent);
            } else if (position == 7) {
                Intent intent = new Intent(MainActivity.this, DesignSupportActivity.class);
                startActivity(intent);
            } else if (position == 8) {
                Intent intent = new Intent(MainActivity.this, FlexBoxLayoutActivity.class);
                startActivity(intent);
            } else if (position == 9) {
                recyclerView.removeHeaderView(headerView2);
            }
        });

        recyclerView.setOnItemLongClickListener((BaseRecyclerAdapter.OnItemLongClickListener<String>) (covertView,
                position, data) -> {
            recyclerAdapter.getDatas().remove(position);
            recyclerAdapter.notifyItemRemoved(position);
            recyclerAdapter.notifyItemRangeChanged(position, recyclerAdapter.getDatas().size() - position);
            return true;
        });

        notifyData(new ArrayList<>(), false);
        recyclerView.refreshWithPull();
        // recyclerView.refresh();//没有下拉刷新效果，直接刷新数据
        // recyclerView.setRefreshing(true);只有下拉刷新效果，不刷新数据
    }

    /**
     * 设置数据Adapter
     */
    public void notifyData(ArrayList<String> list, boolean isClear) {
        if (recyclerAdapter == null) {
            recyclerAdapter = new ZRecyclerAdapter();
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
            if (i == 0) {
                dataList.add("ScrollView");
            } else if (i == 1) {
                dataList.add("EmptyViewLayout");
            } else if (i == 2) {
                dataList.add("GridLayout");
            } else if (i == 3) {
                dataList.add("StaggeredGridLayout");
            } else if (i == 4) {
                dataList.add("MultiTypeLayout");
            } else if (i == 5) {
                dataList.add("SwipeMenuLayout");
            } else if (i == 6) {
                dataList.add("Decoration");
            } else if (i == 7) {
                dataList.add("DesignSupportActivity");
            } else if (i == 8) {
                dataList.add("FlexBoxLayoutActivity");
            } else if (i == 9) {
                dataList.add("移除Header2");
            } else {
                dataList.add(String.format("第%d条数据", i));
            }
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
