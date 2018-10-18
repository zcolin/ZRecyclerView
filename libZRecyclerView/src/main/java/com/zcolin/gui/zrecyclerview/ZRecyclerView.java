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
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zcolin.gui.zrecyclerview.loadmorefooter.DefLoadMoreFooter;
import com.zcolin.gui.zrecyclerview.loadmorefooter.ILoadMoreFooter;
import com.zcolin.gui.zrecyclerview.swiperefreshlayout.SwipeRefreshLayout;
import com.zcolin.gui.zrecyclerview.swiperefreshlayout.ZSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * <p/>
 * RecyclerView下拉刷新和上拉加载更多以及RecyclerView线性、网格、瀑布流
 * <p/>
 */
public class ZRecyclerView extends FrameLayout {
    private PullLoadMoreListener mPullLoadMoreListener;
    private ArrayList<View>      listHeaderView;
    private ArrayList<View>      listFooterView;
    private boolean isRefreshEnabled  = true; //设置下拉刷新是否可用
    private boolean isLoadMoreEnabled = true; //设置到底加载是否可用
    private boolean isShowNoMore      = true; //是否显示 加载全部

    private boolean isAddHeader;//如果在设置adapter之前设置,此变量为false,之后设置,则为true
    private boolean isAddFooter;//如果在设置adapter之前设置,此变量为false,之后设置,则为true
    private boolean isNoMore      = false;
    private boolean isRefreshing  = false;
    private boolean isLoadingData = false;
    private BaseRecyclerAdapter.OnItemClickListener     itemClickListener;
    private BaseRecyclerAdapter.OnItemLongClickListener itemLongClickListener;
    private long minClickIntervaltime = 100; //ITEM点击的最小间隔

    private WrapperRecyclerAdapter wrapAdapter;
    private ILoadMoreFooter        loadMoreFooter;
    private RecyclerView           mRecyclerView;
    private ZSwipeRefreshLayout    mSwipeRefreshLayout;
    private RelativeLayout         mEmptyViewContainer;
    private Context                mContext;
    private RecyclerView.AdapterDataObserver mEmptyDataObserver = new DataObserver();
    private boolean hasRegisterEmptyObserver;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ZRecyclerView(Context context) {
        this(context, null);
    }

    public ZRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRecyclerView(Context context, AttributeSet attrs, int resId) {
        super(context, attrs);
        initView(context, resId);
    }

    private void initView(Context context, int resId) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(resId > 0 ? resId : R.layout.gui_zrecyclerview_zrecycler, null);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayoutOnRefresh(this));
        }

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setVerticalScrollBarEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScroll(this));
        mRecyclerView.setOnTouchListener(new onTouchRecyclerView());
        setLinearLayout(false);

        mEmptyViewContainer = view.findViewById(R.id.emptyView);
        if (mEmptyViewContainer != null) {
            mEmptyViewContainer.setVisibility(View.GONE);
        }

        if (isLoadMoreEnabled) {
            setLoadMoreFooter(new DefLoadMoreFooter(getContext()));
        }

        this.addView(view);
    }

    /**
     * 设置下拉刷新上拉加载回调
     */
    public ZRecyclerView setOnPullLoadMoreListener(PullLoadMoreListener listener) {
        mPullLoadMoreListener = listener;
        return this;
    }

    /**
     * 此处设置OnItemClickListener是调用的{@link BaseRecyclerAdapter#setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener)}，
     * 此处的泛型类型必须和{@link BaseRecyclerAdapter}的相同
     */
    public <T> ZRecyclerView setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener<T> li) {
        itemClickListener = li;
        if (wrapAdapter != null) {
            if (wrapAdapter.getAdapter() instanceof BaseRecyclerAdapter) {
                ((BaseRecyclerAdapter) wrapAdapter.getAdapter()).setOnItemClickListener(li);
            } else {
                throw new IllegalArgumentException("adapter 必须继承BaseRecyclerAdapter 才能使用setOnItemClickListener");
            }
        }
        return this;
    }

    /**
     * 此处设置OnItemLongClickListener是调用的{@link BaseRecyclerAdapter#setOnItemLongClickListener(BaseRecyclerAdapter.OnItemLongClickListener)}，
     * 此处的泛型类型必须和{@link BaseRecyclerAdapter}的相同
     */
    public <T> ZRecyclerView setOnItemLongClickListener(BaseRecyclerAdapter.OnItemLongClickListener<T> li) {
        itemLongClickListener = li;
        if (wrapAdapter != null) {
            if (wrapAdapter.getAdapter() instanceof BaseRecyclerAdapter) {
                ((BaseRecyclerAdapter) wrapAdapter.getAdapter()).setOnItemLongClickListener(li);
            } else {
                throw new IllegalArgumentException("adapter 必须继承BaseRecyclerAdapter 才能使用setOnItemLongClickListener");
            }
        }
        return this;
    }

    /**
     * 设置Item点击的最小间隔
     *
     * @param minClickIntervalTime 最小间隔时间，单位毫秒
     */
    public ZRecyclerView setItemMinClickIntervalTime(long minClickIntervalTime) {
        this.minClickIntervaltime = minClickIntervaltime;
        if (wrapAdapter != null) {
            if (wrapAdapter.getAdapter() instanceof BaseRecyclerAdapter) {
                ((BaseRecyclerAdapter) wrapAdapter.getAdapter()).setItemMinClickIntervalTime(minClickIntervaltime);
            } else {
                throw new IllegalArgumentException("adapter 必须继承BaseRecyclerAdapter 才能使用setItemMinClickIntervalTime");
            }
        }
        return this;
    }

    /**
     * LinearLayoutManager
     *
     * @param isForce 如果已经设置了，是否强制设置
     */
    public ZRecyclerView setLinearLayout(boolean isForce) {
        if (isForce || getLayoutManager() == null || !(getLayoutManager() instanceof LinearLayoutManager)) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        return this;
    }

    /**
     * 设置网格布局管理器
     *
     * @param isForce   如果已经设置了，是否强制设置
     * @param spanCount 列数
     */
    public ZRecyclerView setGridLayout(boolean isForce, int spanCount) {
        if (isForce || getLayoutManager() == null || !(getLayoutManager() instanceof GridLayoutManager)) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
        return this;
    }

    /**
     * 设置瀑布流布局管理器
     *
     * @param isForce   如果已经设置了，是否强制设置
     * @param spanCount 列数
     */
    public ZRecyclerView setStaggeredGridLayout(boolean isForce, int spanCount) {
        if (isForce || getLayoutManager() == null || !(getLayoutManager() instanceof StaggeredGridLayoutManager)) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        }
        return this;
    }

    /**
     * 增加默认分割线
     */
    public ZRecyclerView addDefaultItemDecoration() {
        mRecyclerView.addItemDecoration(new ZRecycleViewDivider(mContext, LinearLayout.HORIZONTAL));
        return this;
    }

    /**
     * 增加分割线
     *
     * @param itemDecoration ex:zRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayout.VERTICAL));
     */
    public ZRecyclerView addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
        return this;
    }

    /**
     * 增加分割线
     *
     * @param itemDecoration ex:zRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayout.VERTICAL), 2);
     */
    public ZRecyclerView addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecyclerView.addItemDecoration(itemDecoration, index);
        return this;
    }


    /**
     * 获取RecyclerVIew的LayoutManager
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    /**
     * 此类为FrameLayout，获取包含的子view - RecyclerView，
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 获取设置的HeaderView的外层LinearLayout
     */
    public View getHeaderLayout() {
        if (wrapAdapter == null) {
            return null;
        } else {
            return wrapAdapter.getHeaderLayout();
        }
    }

    /**
     * 获取设置的FooterView的外层LinearLayout
     */
    public View getFooterLayout() {
        if (wrapAdapter == null) {
            return null;
        } else {
            return wrapAdapter.getFooterLayout();
        }
    }

    /**
     * 设置自定义的HeaderView
     */
    public ZRecyclerView addHeaderView(View headerView) {
        return addHeaderView(headerView, -1);
    }

    /**
     * 设置自定义的HeaderView
     *
     * @param headerViewLayoutId 资源Id
     */
    public ZRecyclerView addHeaderView(Context context, int headerViewLayoutId) {
        return addHeaderView(context, headerViewLayoutId, -1);
    }

    /**
     * 设置自定义的HeaderView
     *
     * @param headerViewLayoutId 资源Id
     * @param index              headerView顺序
     */
    public ZRecyclerView addHeaderView(Context context, int headerViewLayoutId, int index) {
        return addHeaderView(LayoutInflater.from(context).inflate(headerViewLayoutId, null), index);
    }

    /**
     * 设置自定义的HeaderView
     *
     * @param index headerView顺序
     */
    public ZRecyclerView addHeaderView(View headerView, int index) {
        if (headerView != null) {
            if (listHeaderView == null) {
                listHeaderView = new ArrayList<>();
            }

            index = index < 0 ? listHeaderView.size() : index;
            index = index > listHeaderView.size() ? listHeaderView.size() : index;
            headerView.setTag(R.id.srv_reserved_ivew, "reservedView");
            listHeaderView.add(index, headerView);

            if (wrapAdapter != null) {
                wrapAdapter.addHeaderView(headerView, index);
                isAddHeader = true;
            }
        }
        return this;
    }

    /**
     * 设置自定义的FooterView
     */
    public ZRecyclerView addFooterView(View footerView) {
        return addFooterView(footerView, -1);
    }

    /**
     * 设置自定义的FooterView
     */
    public ZRecyclerView addFooterView(Context context, int footerViewLayoutId) {
        return addFooterView(context, footerViewLayoutId, -1);
    }

    /**
     * 设置自定义的FooterView
     */
    public ZRecyclerView addFooterView(Context context, int footerViewLayoutId, int index) {
        return addFooterView(LayoutInflater.from(context).inflate(footerViewLayoutId, null), index);
    }

    /**
     * 设置自定义的FooterView
     */
    public ZRecyclerView addFooterView(View footerView, int index) {
        if (footerView != null) {
            if (listFooterView == null) {
                listFooterView = new ArrayList<>();
            }

            index = index < 0 ? listFooterView.size() : index;
            index = index > listFooterView.size() ? listFooterView.size() : index;
            footerView.setTag(R.id.srv_reserved_ivew, "reservedView");
            listFooterView.add(index, footerView);

            if (wrapAdapter != null) {
                wrapAdapter.addFooterView(footerView, index);
                isAddFooter = true;
            }
        }
        return this;
    }

    public ZRecyclerView removeHeaderView(View header) {
        if (listHeaderView != null) {
            listHeaderView.remove(header);
            if (listHeaderView.size() == 0) {
                listHeaderView = null;
            }
        }

        if (wrapAdapter != null) {
            wrapAdapter.removeHeaderView(header);
        }
        return this;
    }

    public ZRecyclerView removeAllHeaderView() {
        if (listHeaderView != null) {
            listHeaderView.clear();
            listHeaderView = null;
        }

        if (wrapAdapter != null) {
            wrapAdapter.removeAllHeaderView();
        }
        return this;
    }

    public ZRecyclerView removeFooterView(View footer) {
        if (listFooterView != null) {
            listFooterView.remove(footer);
            if (listFooterView.size() == 0) {
                listFooterView = null;
            }
        }

        if (wrapAdapter != null) {
            wrapAdapter.removeFooterView(footer);
        }
        return this;
    }

    public ZRecyclerView removeAllFooterView() {
        if (listFooterView != null) {
            listFooterView.clear();
            listFooterView = null;
        }

        if (wrapAdapter != null) {
            wrapAdapter.removeAllFooterView();
        }
        return this;
    }

    /**
     * 获取加载更多的Footer
     */
    public View getLoadMoreFooterView() {
        return loadMoreFooter.getFootView();
    }

    /**
     * 设置自定义的加载更多FooterView
     */
    public ZRecyclerView setLoadMoreFooter(ILoadMoreFooter loadMoreFooter) {
        this.loadMoreFooter = loadMoreFooter;
        this.loadMoreFooter.getFootView().setTag(R.id.srv_reserved_ivew, "reservedView");
        return this;
    }

    /**
     * 设置到底加载是否可用
     */
    public ZRecyclerView setIsLoadMoreEnabled(boolean enabled) {
        isLoadMoreEnabled = enabled;
        if (!enabled && loadMoreFooter != null) {
            loadMoreFooter.onReset();
        }
        return this;
    }

    /**
     * 到底加载是否可用
     */
    public boolean isLoadMoreEnabled() {
        return isLoadMoreEnabled;
    }

    /**
     * 设置下拉刷新是否可用
     */
    public ZRecyclerView setIsRefreshEnabled(boolean enabled) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enabled);
            isRefreshEnabled = enabled;
        }
        return this;
    }

    /**
     * 下拉刷新是否可用
     */
    public boolean isRefreshEnabled() {
        return isRefreshEnabled;
    }

    /**
     * 设置是否显示NoMoreView
     */
    public ZRecyclerView setIsShowNoMore(boolean isShowNoMore) {
        this.isShowNoMore = isShowNoMore;
        return this;
    }

    /**
     * 是否显示NoMoreView
     */
    public boolean isShowNoMore() {
        return isShowNoMore;
    }

    /**
     * 设置加载进度条View,此处可以设置 https://github.com/81813780/AVLoadingIndicatorView 的view
     */
    public ZRecyclerView setLoadMoreProgressView(View view) {
        if (loadMoreFooter != null && loadMoreFooter instanceof DefLoadMoreFooter) {
            ((DefLoadMoreFooter) loadMoreFooter).setProgressView(view);
        }
        return this;
    }

    /**
     * 设置加载进度条文字（如果LoadMoreFooter为DefLoadMoreFooter）
     *
     * @param text1 加载中
     * @param text2 加载完成
     * @param text3 已加载全部
     */
    public ZRecyclerView setLoadMoreText(String text1, String text2, String text3) {
        if (loadMoreFooter != null && loadMoreFooter instanceof DefLoadMoreFooter) {
            ((DefLoadMoreFooter) loadMoreFooter).setText(text1, text2, text3);
        }
        return this;
    }

    /**
     * 设置加载进度条文字颜色（如果LoadMoreFooter为DefLoadMoreFooter）
     */
    public ZRecyclerView setLoadMoreTextColor(int textColor) {
        if (loadMoreFooter != null && loadMoreFooter instanceof DefLoadMoreFooter) {
            ((DefLoadMoreFooter) loadMoreFooter).setTextColor(textColor);
        }
        return this;
    }

    /**
     * 设置加载进度条文字大小（如果LoadMoreFooter为DefLoadMoreFooter）
     */
    public ZRecyclerView setLoadMoreTextSize(float textSize) {
        if (loadMoreFooter != null && loadMoreFooter instanceof DefLoadMoreFooter) {
            ((DefLoadMoreFooter) loadMoreFooter).setTextSize(textSize);
        }
        return this;
    }

    /**
     * 设置没有数据时显示的EmptyView
     */
    public ZRecyclerView setEmptyView(View emptyView) {
        return setEmptyView(emptyView, RelativeLayout.CENTER_IN_PARENT);
    }

    /**
     * 设置没有数据时显示的EmptyView
     */
    public ZRecyclerView setEmptyView(Context context, int layoutId) {
        return setEmptyView(LayoutInflater.from(context).inflate(layoutId, null), RelativeLayout.CENTER_IN_PARENT);
    }

    /**
     * 设置没有数据时显示的EmptyView
     *
     * @param gravity {@link android.view.Gravity}  ex RelativeLayout.CENTER_IN_PARENT
     */
    public ZRecyclerView setEmptyView(Context context, int layoutId, int gravity) {
        return setEmptyView(LayoutInflater.from(context).inflate(layoutId, null), gravity);
    }

    /**
     * 设置没有数据时显示的EmptyView
     *
     * @param gravity {@link RelativeLayout}  ex RelativeLayout.CENTER_IN_PARENT
     */
    public ZRecyclerView setEmptyView(View emptyView, int gravity) {
        if (mEmptyViewContainer != null) {
            mEmptyViewContainer.removeAllViews();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (gravity != 0) {
                params.addRule(gravity);
            }
            mEmptyViewContainer.addView(emptyView, params);
        }
        return this;
    }

    /**
     * 返回的放置emptyView的RelativeLayout
     */
    public View getEmptyViewContainer() {
        return mEmptyViewContainer;
    }

    /**
     * 返回的放置emptyView的RelativeLayout
     */
    public View getEmptyView() {
        if (mEmptyViewContainer != null) {
            return mEmptyViewContainer.getChildCount() > 0 ? mEmptyViewContainer.getChildAt(0) : null;
        }
        return null;
    }

    /**
     * 使RecyclerView滚动到顶部
     */
    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    /**
     * 使RecyclerView滚动到顶部
     */
    public void scrollToBottom() {
        mRecyclerView.smoothScrollToPosition(getHeight());
    }


    public boolean isSwipeRefreshEnable() {
        if (mSwipeRefreshLayout != null) {
            return mSwipeRefreshLayout.isEnabled();
        }
        return false;
    }

    public void setSwipeRefreshEnable(boolean enable) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }

    /**
     * 设置SwipeRefreshLayout的colorScheme
     */
    public ZRecyclerView setColorSchemeResources(int... colorResIds) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(colorResIds);
        }
        return this;
    }

    /**
     * 此类为FrameLayout，获取包含的子view - SwipeRefreshLayout，
     */
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    /**
     * 设置是否处理冲突（如Viewpager），默认不处理
     * 如果嵌套有viewpager等控件，需要设置isProceeConflict为true
     */
    public void setIsProceeConflict(boolean isProceeConflict) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setIsProceeConflict(isProceeConflict);
        }
    }

    /**
     * 手动调用直接刷新，无下拉效果
     */
    public void refresh() {
        if (mPullLoadMoreListener != null) {
            isRefreshing = true;
            mPullLoadMoreListener.onRefresh();
        }
    }

    /**
     * 手动调用下拉刷新，有下拉效果
     */
    public void refreshWithPull() {
        if (mSwipeRefreshLayout != null) {
            handler.postDelayed(() -> {
                if (isRefreshEnabled) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    refresh();
                }
            }, 300);
        }
    }

    /**
     * 手动调用加载状态，此函数不会调用 {@link PullLoadMoreListener#onRefresh()}加载数据
     * 如果需要加载数据和状态显示调用 {@link #refreshWithPull()}
     */
    public void setRefreshing(final boolean isRefresh) {
        if (mSwipeRefreshLayout != null) {
            handler.post(() -> {
                if (isRefreshEnabled) {
                    isRefreshing = isRefresh;
                    mSwipeRefreshLayout.setRefreshing(isRefresh);
                }
            });
        }
    }

    /**
     * 手动调用直接加载更多
     */
    public void loadMore() {
        if (mPullLoadMoreListener != null && !isNoMore) {
            this.isLoadingData = true;
            mPullLoadMoreListener.onLoadMore();
        }
    }

    /**
     * 手动调用直接加载更多
     */
    public void loadMoreWithLoading() {
        if (mPullLoadMoreListener != null && !isNoMore) {
            this.isLoadingData = true;
            loadMoreFooter.onLoading();
            mPullLoadMoreListener.onLoadMore();
        }
    }

    /**
     * 下拉刷新和到底加载完成
     */
    public void setPullLoadMoreCompleted() {
        if (isRefreshing) {
            isRefreshing = false;
            setRefreshing(false);
        }

        isLoadingData = false;
        //已经设置了nomore了，就不在用完成状态覆盖nomore状态
        if (!isNoMore) {
            loadMoreFooter.onComplete();
        }
    }

    /**
     * 设置是否已加载全部<br>
     * 设置之后到底{@link PullLoadMoreListener#onLoadMore()}不会再调用除非再次调用{@link #setNoMore(boolean)}为true;
     * 一般做法是在{@link PullLoadMoreListener#onRefresh()}中设置{@link #setNoMore(boolean)}为true;
     */
    public void setNoMore(boolean noMore) {
        setNoMore(noMore, 0, 0);
    }

    /**
     * 设置是否已加载全部, <br>
     * 设置之后到底{@link PullLoadMoreListener#onLoadMore()}不会再调用除非再次调用{@link #setNoMore(boolean)}为true;
     * 一般做法是在{@link PullLoadMoreListener#onRefresh()}中设置{@link #setNoMore(boolean)}为true;
     */
    public void setNoMore(boolean noMore, int minShowItem, List<?> data) {
        setNoMore(noMore, minShowItem, data == null ? 0 : data.size());
    }

    /**
     * 设置是否已加载全部, <br>
     * 设置之后到底{@link PullLoadMoreListener#onLoadMore()}不会再调用除非再次调用{@link #setNoMore(boolean)}为true;
     * 一般做法是在{@link PullLoadMoreListener#onRefresh()}中设置{@link #setNoMore(boolean)}为true;
     */
    public void setNoMore(boolean noMore, int minShowItem, int dataSize) {
        isLoadingData = false;
        isNoMore = noMore;

        if (isNoMore && dataSize >= minShowItem) {
            loadMoreFooter.onNoMore();
        } else {
            loadMoreFooter.onComplete();
        }
    }

    public boolean isNoMore() {
        return isNoMore;
    }

    public boolean isLoadingData() {
        return isLoadingData;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (wrapAdapter != null && wrapAdapter.getAdapter() != null && hasRegisterEmptyObserver) {
            wrapAdapter.getAdapter().unregisterAdapterDataObserver(mEmptyDataObserver);
            hasRegisterEmptyObserver = false;
        }

        wrapAdapter = new WrapperRecyclerAdapter(adapter);
        wrapAdapter.setLoadMoreFooter(loadMoreFooter).setIsShowNoMore(isShowNoMore).setIsLoadMoreEnabled(isLoadMoreEnabled);

        if (!isAddHeader) {
            wrapAdapter.setHeaderViews(listHeaderView);
        }

        if (!isAddFooter) {
            wrapAdapter.setFooterViews(listFooterView);
        }

        mRecyclerView.setAdapter(wrapAdapter);

        setOnItemClickListener(itemClickListener);
        setOnItemLongClickListener(itemLongClickListener);
        setItemMinClickIntervalTime(minClickIntervaltime);
        if (!hasRegisterEmptyObserver) {
            adapter.registerAdapterDataObserver(mEmptyDataObserver);
            hasRegisterEmptyObserver = true;
        }
    }


    /**
     * Solve IndexOutOfBoundsException exception
     */
    public class onTouchRecyclerView implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return isRefreshing || isLoadingData;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        /*设置emptyView的监听者*/
        if (wrapAdapter != null && wrapAdapter.getAdapter() != null && !hasRegisterEmptyObserver && mEmptyDataObserver != null) {
            wrapAdapter.getAdapter().registerAdapterDataObserver(mEmptyDataObserver);
            hasRegisterEmptyObserver = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        
        /*注销监听者*/
        if (wrapAdapter != null && wrapAdapter.getAdapter() != null && hasRegisterEmptyObserver) {
            wrapAdapter.getAdapter().unregisterAdapterDataObserver(mEmptyDataObserver);
            hasRegisterEmptyObserver = false;
        }
    }

    /**
     * emptyView的监听者类
     */
    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            checkEmptyView();
            wrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            wrapAdapter.notifyItemRangeInserted(wrapAdapter.getHeaderLayout() == null ? positionStart : positionStart + 1, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            wrapAdapter.notifyItemRangeRemoved(wrapAdapter.getHeaderLayout() == null ? positionStart : positionStart + 1, itemCount);
            checkEmptyView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            int from = wrapAdapter.getHeaderLayout() == null ? fromPosition : fromPosition + 1;
            int to = wrapAdapter.getHeaderLayout() == null ? toPosition : toPosition + 1;
            wrapAdapter.notifyItemMoved(from, to);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            wrapAdapter.notifyItemRangeChanged(wrapAdapter.getHeaderLayout() == null ? positionStart : positionStart + 1, itemCount);
            checkEmptyView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            wrapAdapter.notifyItemRangeChanged(wrapAdapter.getHeaderLayout() == null ? positionStart : positionStart + 1, itemCount, payload);
            checkEmptyView();
        }

        private void checkEmptyView() {
            if (mEmptyViewContainer != null && mEmptyViewContainer.getChildCount() > 0) {
                if (wrapAdapter.getAdapter().getItemCount() == 0) {
                    mEmptyViewContainer.setVisibility(View.VISIBLE);

                    //使emptyview居中（除headerview之外）
                    if (wrapAdapter.getHeaderLayout() != null && mEmptyViewContainer.getLayoutParams() instanceof MarginLayoutParams) {
                        if (wrapAdapter.getHeaderLayout().getHeight() == 0 && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            wrapAdapter.getHeaderLayout().measure(0, 0);
                            ((MarginLayoutParams) mEmptyViewContainer.getLayoutParams()).topMargin = wrapAdapter.getHeaderLayout().getMeasuredHeight();
                        } else {
                            ((MarginLayoutParams) mEmptyViewContainer.getLayoutParams()).topMargin = wrapAdapter.getHeaderLayout().getHeight();
                        }
                    }
                } else {
                    mEmptyViewContainer.setVisibility(View.GONE);
                }
            }
        }
    }

    class SwipeRefreshLayoutOnRefresh implements ZSwipeRefreshLayout.OnRefreshListener {
        private ZRecyclerView zRecyclerView;

        public SwipeRefreshLayoutOnRefresh(ZRecyclerView pullLoadMoreRecyclerView) {
            this.zRecyclerView = pullLoadMoreRecyclerView;
        }

        @Override
        public void onRefresh() {
            if (!zRecyclerView.isRefreshing()) {
                zRecyclerView.refresh();
            }
        }
    }

    public interface PullLoadMoreListener {
        void onRefresh();

        void onLoadMore();
    }
}
