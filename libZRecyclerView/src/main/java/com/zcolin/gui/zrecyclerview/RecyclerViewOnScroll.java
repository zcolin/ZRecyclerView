/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-1-9 下午2:46
 * ********************************************************
 */

package com.zcolin.gui.zrecyclerview;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * RecyclerView的ScrollListener的滚动监听代理
 */
public class RecyclerViewOnScroll extends RecyclerView.OnScrollListener {
    private ZRecyclerView mPullLoadMoreRecyclerView;

    public RecyclerViewOnScroll(ZRecyclerView pullLoadMoreRecyclerView) {
        this.mPullLoadMoreRecyclerView = pullLoadMoreRecyclerView;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastCompletelyVisibleItem = 0;
        int firstVisibleItem = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
            //Position to find the final item of the current LayoutManager
            lastCompletelyVisibleItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();
            firstVisibleItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) layoutManager);
            lastCompletelyVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = ((StaggeredGridLayoutManager) layoutManager);
            // since may lead to the final item has more than one StaggeredGridLayoutManager the particularity of the so here that is an array
            // this array into an array of position and then take the maximum value that is the last show the position value
            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
            lastCompletelyVisibleItem = findMax(lastPositions);
            firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions)[0];
        }

        if (firstVisibleItem == 0 || firstVisibleItem == RecyclerView.NO_POSITION) {
            if (mPullLoadMoreRecyclerView.isRefreshEnabled()) {
                mPullLoadMoreRecyclerView.setSwipeRefreshEnable(true);
            }
        } else {
            mPullLoadMoreRecyclerView.setSwipeRefreshEnable(false);
        }
        if (mPullLoadMoreRecyclerView.isLoadMoreEnabled() && !mPullLoadMoreRecyclerView.isRefreshing() && !mPullLoadMoreRecyclerView.isNoMore() && 
                (lastCompletelyVisibleItem == totalItemCount - 1) && !mPullLoadMoreRecyclerView
                .isLoadingData() && (dx > 0 || dy > 0)) {
            mPullLoadMoreRecyclerView.loadMoreWithLoading();
        }

    }

    //To find the maximum value in the array
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
