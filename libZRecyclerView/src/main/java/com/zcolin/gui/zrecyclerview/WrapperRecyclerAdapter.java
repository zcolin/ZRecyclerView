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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zcolin.gui.zrecyclerview.loadmorefooter.DefLoadMoreFooter;
import com.zcolin.gui.zrecyclerview.loadmorefooter.ILoadMoreFooter;

import java.util.ArrayList;


/**
 * 可以设置Header并且封装了基本方法的的Adapter
 */
class WrapperRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final  int TYPE_HEADER          = 100002;
    public static final  int TYPE_FOOTER          = 100003;
    private static final int TYPE_LOADMORE_FOOTER = 100004;

    private LinearLayout    mHeaderLayout;
    private LinearLayout    mFooterLayout;
    private LinearLayout    mCopyHeaderLayout = null;
    private LinearLayout    mCopyFooterLayout = null;
    private ILoadMoreFooter loadMoreFooter;
    private boolean         isLoadMoreEnabled = true;//设置到底加载是否可用

    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    public WrapperRecyclerAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.adapter = adapter;
        if (adapter == null) {
            throw new IllegalArgumentException("adapter cant be null");
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public LinearLayout getHeaderLayout() {
        return mHeaderLayout;
    }

    public LinearLayout getFooterLayout() {
        return mFooterLayout;
    }

    public WrapperRecyclerAdapter setHeaderViews(ArrayList<View> listView) {
        if (mHeaderLayout != null) {
            mHeaderLayout.removeAllViews();
        }

        if (listView != null) {
            for (View view : listView) {
                addHeaderViewWithoutNotify(view, -1);
            }
            adapter.notifyDataSetChanged();
        } else {
            removeAllHeaderView();
        }
        return this;
    }

    public WrapperRecyclerAdapter addHeaderView(View header, int index) {
        addHeaderViewWithoutNotify(header, index);
        adapter.notifyDataSetChanged();
        return this;
    }

    public WrapperRecyclerAdapter addHeaderViewWithoutNotify(View header, int index) {
        if (mHeaderLayout == null) {
            if (mCopyHeaderLayout == null) {
                mHeaderLayout = new LinearLayout(header.getContext());
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                                                                            RecyclerView.LayoutParams.WRAP_CONTENT));
                mCopyHeaderLayout = mHeaderLayout;
            } else {
                mHeaderLayout = mCopyHeaderLayout;
            }
        }
        index = index >= mHeaderLayout.getChildCount() ? -1 : index;
        mHeaderLayout.addView(header, index);
        return this;
    }


    public WrapperRecyclerAdapter setFooterViews(ArrayList<View> listView) {
        if (mFooterLayout != null) {
            mFooterLayout.removeAllViews();
        }

        if (listView != null) {
            for (View view : listView) {
                addFooterViewWithoutNotify(view, -1);
            }
            adapter.notifyDataSetChanged();
        } else {
            removeAllFooterView();
        }
        return this;
    }

    public WrapperRecyclerAdapter addFooterView(View footer, int index) {
        addFooterViewWithoutNotify(footer, index);
        adapter.notifyDataSetChanged();
        return this;
    }

    public WrapperRecyclerAdapter addFooterViewWithoutNotify(View footer, int index) {
        if (mFooterLayout == null) {
            if (mCopyFooterLayout == null) {
                mFooterLayout = new LinearLayout(footer.getContext());
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                                                                            RecyclerView.LayoutParams.WRAP_CONTENT));
                mCopyFooterLayout = mFooterLayout;
            } else {
                mFooterLayout = mCopyFooterLayout;
            }
        }
        index = index >= mFooterLayout.getChildCount() ? -1 : index;
        mFooterLayout.addView(footer, index);
        return this;
    }

    public void removeHeaderView(View header) {
        if (mHeaderLayout == null) {
            return;
        }

        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            mHeaderLayout = null;
        }
        adapter.notifyDataSetChanged();
    }

    public void removeFooterView(View footer) {
        if (mFooterLayout == null) {
            return;
        }

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            mFooterLayout = null;
        }
        adapter.notifyDataSetChanged();
    }

    public void removeAllHeaderView() {
        if (mHeaderLayout == null) {
            return;
        }

        mHeaderLayout.removeAllViews();
        mHeaderLayout = null;
        adapter.notifyDataSetChanged();
    }

    public void removeAllFooterView() {
        if (mFooterLayout == null) {
            return;
        }

        mFooterLayout.removeAllViews();
        mFooterLayout = null;
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置自定义的FooterView
     */
    public WrapperRecyclerAdapter setLoadMoreFooter(ILoadMoreFooter loadMoreFooter) {
        this.loadMoreFooter = loadMoreFooter;
        return this;
    }

    /**
     * 到底加载是否可用
     */
    public WrapperRecyclerAdapter setIsLoadMoreEnabled(boolean enabled) {
        isLoadMoreEnabled = enabled;
        if (!enabled && loadMoreFooter != null) {
            loadMoreFooter.onComplete();
        }
        return this;
    }

    /**
     * 到底加载是否可用
     */
    public WrapperRecyclerAdapter setIsShowNoMore(boolean isShow) {
        if (loadMoreFooter != null) {
            loadMoreFooter.setIsShowNoMore(isShow);
        }
        return this;
    }


    /**
     * 设置加载更多的进度条View
     */
    public WrapperRecyclerAdapter setLoadMoreProgressView(View view) {
        if (loadMoreFooter != null && loadMoreFooter instanceof DefLoadMoreFooter) {
            ((DefLoadMoreFooter) loadMoreFooter).setProgressView(view);
        }
        return this;
    }

    /**
     * 获取在数据集中的真实位置，而不是在Recycle中包含Header和Footer的位置
     */
    public int getRealPosition(int position) {
        return position - getHeaderViewCount();
    }

    @Override
    public int getItemCount() {
        int count = adapter.getItemCount();
        count += isLoadMoreEnabled ? 1 : 0;
        count += getHeaderViewCount();
        count += getFooterViewCount();
        return count;
    }

    /**
     * @return 返回真实条目数据，不包含Header和Footer
     */
    public int getRealItemCount() {
        return adapter.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        int realPosition = getRealPosition(position);
        if (realPosition >= 0 && realPosition < adapter.getItemCount()) {
            return adapter.getItemId(realPosition);
        }
        return -1;
    }

    private boolean isReservedItemType(int itemViewType) {
        return itemViewType == TYPE_LOADMORE_FOOTER || itemViewType == TYPE_HEADER || itemViewType == TYPE_FOOTER;
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadMoreFooter(position)) {
            return TYPE_LOADMORE_FOOTER;
        } else if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        }

        int realPosition = getRealPosition(position);
        if (realPosition >= 0 && realPosition < adapter.getItemCount()) {
            if (isReservedItemType(adapter.getItemViewType(realPosition))) {
                throw new IllegalStateException(
                        "ZRecyclerView require itemViewType in adapter should be less than 10000 ");
            }

            return adapter.getItemViewType(realPosition);
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == TYPE_LOADMORE_FOOTER) {
            return new SimpleViewHolder(loadMoreFooter.getFootView());
        } else if (mHeaderLayout != null && viewType == TYPE_HEADER) {
            return new SimpleViewHolder(mHeaderLayout);
        } else if (mFooterLayout != null && viewType == TYPE_FOOTER) {
            return new SimpleViewHolder(mFooterLayout);
        }

        return adapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (isReservedItemType(getItemViewType(position))) {
            return;
        }

        int realPosition = getRealPosition(position);
        if (realPosition >= 0 && realPosition < adapter.getItemCount()) {
            adapter.onBindViewHolder(viewHolder, realPosition);
        }
    }


    public boolean isLoadMoreFooter(int position) {
        return isLoadMoreEnabled && position == getItemCount() - 1;
    }


    public boolean isHeaderView(int position) {
        return position == 0 && mHeaderLayout != null;
    }

    public boolean isFooterView(int position) {
        return mFooterLayout != null && ((isLoadMoreEnabled && position == getItemCount() - 2) || (!isLoadMoreEnabled && position == getItemCount() - 1));
    }

    public int getHeaderViewCount() {
        return mHeaderLayout == null ? 0 : 1;
    }

    public int getFooterViewCount() {
        return mFooterLayout == null ? 0 : 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int fullCount = gridManager.getSpanCount();
                    if (isReservedItemType(getItemViewType(position))) {
                        return fullCount;
                    } else if (adapter instanceof BaseRecyclerAdapter) {
                        int count = ((BaseRecyclerAdapter) adapter).getGridItemSpanCount(getRealPosition(position),
                                                                                         getItemViewType(position));
                        count = (count == 0 || count > fullCount) ? fullCount : count;
                        return count;
                    }
                    return 1;
                }
            });
        }
        adapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        if (null == adapter) {
            return;
        }
        adapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        int position = holder.getLayoutPosition();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            if (isReservedItemType(getItemViewType(position)) || (adapter instanceof BaseRecyclerAdapter && ((BaseRecyclerAdapter) adapter)
                    .getIsStaggeredItemFullSpan(getRealPosition(position), getItemViewType(position)))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
        adapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        int position = holder.getAdapterPosition();
        if (isHeaderView(position) || isFooterView(position)) {
            return;
        }

        adapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        int position = holder.getAdapterPosition();
        if (isHeaderView(position) || isFooterView(position)) {
            return;
        }

        adapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return adapter.onFailedToRecycleView(holder);
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }
}