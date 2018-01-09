/*
 * *********************************************************
 *   author   colin
 *   company  telchina
 *   email    wanglin2046@126.com
 *   date     18-1-9 下午2:46
 * ********************************************************
 */

package com.zcolin.gui.zrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import com.zcolin.gui.zrecyclerview.swipemenu.SwipeMenuLayout;
import com.zcolin.gui.zrecyclerview.swipemenu.SwipeMenuRecyclerView;


/**
 * 支持下拉、上拉、侧拉菜单的RecyclerView
 */
public class ZSwipeMenuRecyclerView extends ZRecyclerView {
    private SwipeMenuRecyclerView swipeMenuRecyclerView;

    public ZSwipeMenuRecyclerView(Context context) {
        this(context, null);
    }

    public ZSwipeMenuRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.gui_zrecyclerview_swipemenu_pullrecycler);
        swipeMenuRecyclerView = (SwipeMenuRecyclerView) getRecyclerView();
        setIsProceeConflict(true);
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        swipeMenuRecyclerView.setCloseInterpolator(interpolator);
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        swipeMenuRecyclerView.setOpenInterpolator(interpolator);
    }

    public Interpolator getOpenInterpolator() {
        return swipeMenuRecyclerView.getOpenInterpolator();
    }

    public Interpolator getCloseInterpolator() {
        return swipeMenuRecyclerView.getCloseInterpolator();
    }

    /**
     * open menu manually
     *
     * @param position the adapter position
     */
    public void smoothOpenMenu(int position) {
        swipeMenuRecyclerView.smoothOpenMenu(position);
    }

    /**
     * close the opened menu manually
     */
    public void smoothCloseMenu() {
        swipeMenuRecyclerView.smoothCloseMenu();
    }

    public void setOnSwipeListener(SwipeMenuRecyclerView.OnSwipeListener onSwipeListener) {
        swipeMenuRecyclerView.setOnSwipeListener(onSwipeListener);
    }

    /**
     * get current touched view
     *
     * @return touched view, maybe null
     */
    public SwipeMenuLayout getTouchView() {
        return swipeMenuRecyclerView.getTouchView();
    }


    /**
     * set the swipe direction
     *
     * @param direction swipe direction (left or right)
     */
    public void setSwipeDirection(int direction) {
        swipeMenuRecyclerView.setSwipeDirection(direction);
    }
}
