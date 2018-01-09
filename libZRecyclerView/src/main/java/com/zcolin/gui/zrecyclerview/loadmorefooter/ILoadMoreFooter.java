/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-1-9 下午2:46
 * ********************************************************
 */

package com.zcolin.gui.zrecyclerview.loadmorefooter;

import android.view.View;

/**
 * 加载更多FooterView需要实现的接口
 */
public interface ILoadMoreFooter {
    /**
     * 状态回调，回复初始设置
     */
    void onReset();

    /**
     * 状态回调，加载中
     */
    void onLoading();

    /**
     * 状态回调，加载完成
     */
    void onComplete();

    /**
     * 状态回调，已全部加载完成
     */
    void onNoMore();

    /**
     * 是否显示没有更多
     */
    void setIsShowNoMore(boolean isShow);

    /**
     * 加载更多的View
     */
    View getFootView();
}
