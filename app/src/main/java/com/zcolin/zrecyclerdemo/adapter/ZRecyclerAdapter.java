package com.zcolin.zrecyclerdemo.adapter;


import android.widget.ImageView;
import android.widget.TextView;

import com.zcolin.gui.zrecyclerview.BaseRecyclerAdapter;
import com.zcolin.zrecyclerdemo.R;


public class ZRecyclerAdapter extends BaseRecyclerAdapter<String> {

    public static final int TYPE_CARDVIEW = 0;
    public static final int TYPE_LISTVIEW = 1;
    public static final int TYPE_FLEXBOX  = 2;

    public int showType = TYPE_CARDVIEW;

    public ZRecyclerAdapter() {
    }

    public ZRecyclerAdapter(int showType) {
        this.showType = showType;
    }

    @Override
    public int getItemLayoutId(int viewType) {
        switch (showType) {
            case TYPE_CARDVIEW:
                return R.layout.view_recycler_item;
            case TYPE_LISTVIEW:
                return R.layout.view_recycler_item_1;
            case TYPE_FLEXBOX:
                return R.layout.view_recycler_item_3;
        }
        return R.layout.view_recycler_item;
    }

    @Override
    public void setUpData(CommonHolder holder, int position, int viewType, String data) {
        TextView textView = getView(holder, R.id.textView);
        ImageView imageView = getView(holder, R.id.imageView);
        imageView.setImageResource(R.drawable.ic_launcher);
        textView.setText(data);
    }
}