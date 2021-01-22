package com.zcolin.gui.zrecyclerview;

import android.content.Context;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 避免在RecyclerView中使用FlexboxLayoutManager时，报错：
 * java.lang.ClassCastException: androidx.recyclerview.widget.RecyclerView$LayoutParams cannot be cast to com.google
 * .android.flexbox.FlexItem
 */
public class ZFlexboxLayoutManager extends FlexboxLayoutManager {

    public ZFlexboxLayoutManager(Context context) {
        super(context);
    }

    public ZFlexboxLayoutManager(Context context, int flexDirection) {
        super(context, flexDirection);
    }

    public ZFlexboxLayoutManager(Context context, int flexDirection, int flexWrap) {
        super(context, flexDirection, flexWrap);
    }

    /**
     * 将LayoutParams转换成新的FlexboxLayoutManager.LayoutParams
     */
    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof RecyclerView.LayoutParams) {
            return new FlexboxLayoutManager.LayoutParams((RecyclerView.LayoutParams) lp);
        } else if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new FlexboxLayoutManager.LayoutParams((ViewGroup.MarginLayoutParams) lp);
        } else {
            return new FlexboxLayoutManager.LayoutParams(lp);
        }
    }
}