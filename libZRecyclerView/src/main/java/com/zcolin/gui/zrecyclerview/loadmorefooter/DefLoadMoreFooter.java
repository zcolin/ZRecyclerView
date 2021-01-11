/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-1-9 下午2:46
 * ********************************************************
 */

package com.zcolin.gui.zrecyclerview.loadmorefooter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 默认的加载更多FooterView，如需要简单的变换，可以直接在{@link com.zcolin.gui.zrecyclerview.ZRecyclerView}中设置
 * 复杂的需要继承此类重写或者实现{@link ILoadMoreFooter} 接口
 */
public class DefLoadMoreFooter extends LinearLayout implements ILoadMoreFooter {

    public String STR_LOADING       = "正在加载";
    public String STR_LOAD_COMPLETE = "正在加载";
    public String STR_NOMORE        = "已加载全部";

    private SimpleViewSwitcher mProgressBar;
    private TextView           mText;
    private boolean isShowNoMore = true;
    private int mMeasuredHeight;

    public DefLoadMoreFooter(Context context) {
        this(context, null);
    }

    public DefLoadMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {
        setGravity(Gravity.CENTER);
        setPadding(0, 25, 0, 25);
        setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setProgressView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall));

        mText = new TextView(getContext());
        mText.setText(STR_LOADING);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 0, 0, 0);
        addView(mText, layoutParams);

        onReset();//初始为隐藏状态

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }


    /**
     * 设置加载进度条View,此处可以设置 https://github.com/81813780/AVLoadingIndicatorView 的view
     */
    public void setProgressView(View view) {
        if (mProgressBar == null) {
            mProgressBar = new SimpleViewSwitcher(getContext());
            mProgressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(mProgressBar);
        }

        mProgressBar.setView(view);
    }

    /**
     * 设置加载进度条文字
     *
     * @param text1 加载中
     * @param text2 加载完成
     * @param text3 已加载全部
     */
    public void setText(String text1, String text2, String text3) {
        STR_LOADING = text1;
        STR_LOAD_COMPLETE = text2;
        STR_NOMORE = text3;
    }

    /**
     * 设置加载进度条文字颜色
     */
    public void setTextColor(int color) {
        mText.setTextColor(color);
    }


    /**
     * 设置加载进度条文字大小
     */
    public void setTextSize(float textSize) {
        mText.setTextSize(textSize);
    }

    @Override
    public void setIsShowNoMore(boolean isShow) {
        isShowNoMore = isShow;
    }

    @Override
    public void onReset() {
        onComplete();
    }

    @Override
    public void onLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mText.setText(STR_LOADING);
        this.getLayoutParams().height = mMeasuredHeight;//动态设置高度，否则在列表中会占位高度
        this.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComplete() {
        mText.setText(STR_LOAD_COMPLETE);
        this.getLayoutParams().height = mMeasuredHeight;
        this.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNoMore() {
        mText.setText(STR_NOMORE);
        mProgressBar.setVisibility(View.GONE);
        this.setVisibility(isShowNoMore ? View.VISIBLE : View.GONE);
        this.getLayoutParams().height = isShowNoMore ? mMeasuredHeight : 5;
    }

    @Override
    public View getFootView() {
        return this;
    }
}
