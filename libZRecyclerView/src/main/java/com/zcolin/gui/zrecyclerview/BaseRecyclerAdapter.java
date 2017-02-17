/*
 * *********************************************************
 *   author   colin
 *   company  fosung
 *   email    wanglin2046@126.com
 *   date     16-12-19 上午11:56
 * ********************************************************
 */

package com.zcolin.gui.zrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以设置Header并且封装了基本方法的的Adapter
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.CommonHolder> {

    private ArrayList<T> listData = new ArrayList<>();
    private OnItemClickListener<T>     itemClickListener;
    private OnItemLongClickListener<T> itemLongClickListener;
    private long minClickIntervaltime = 100; //ITEM点击的最小间隔
    private long mLastClickTime;//上次点击时间

    /**
     * 获取布局ID
     *
     * @return 布局Id,  ex:R.layout.listitem_***
     */
    public abstract int getItemLayoutId(int viewType);

    /**
     * 客户自己定义的GridLayoutManager的item需要跨的行数
     *
     * @return GridLayoutManager 0为占满，其他为占的具体行数,最多为占满
     */
    public int getGridItemSpanCount(int position, int viewType) {
        return 1;
    }

    /**
     * 客户自己定义的StaggeredLayoutManager的item是否需要占满
     *
     * @return true为占满, false为1行
     */
    public boolean getIsStaggeredItemFullSpan(int position, int viewType) {
        return false;
    }

    /**
     * 设置Item点击的最小间隔
     *
     * @param minClickIntervaltime millionSeconds
     */
    public void setItemMinClickIntervalTime(long minClickIntervaltime) {
        this.minClickIntervaltime = minClickIntervaltime;
    }

    /**
     * 设置显示数据,替代getView，在此函数中进行赋值操作
     * <p>
     * ex:
     * TextView tvNumb = getView(view, R.id.tv);
     * tvNumb.setText(String.valueOf(position + 1));
     */
    public abstract void setUpData(CommonHolder holder, int position, int viewType, T data);

    public void setOnItemClickListener(OnItemClickListener<T> li) {
        itemClickListener = li;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> li) {
        itemLongClickListener = li;
    }

    /**
     * 追加条目数据
     */
    public void addDatas(List<T> datas) {
        if (datas != null) {
            listData.addAll(datas);
        }
        notifyDataSetChanged();
    }

    /**
     * 将数据替换为传入的数据集
     */
    public void setDatas(List<T> datas) {
        listData.clear();
        if (datas != null) {
            listData.addAll(datas);
        }
        notifyDataSetChanged();
    }

    /**
     * 清空数据集
     */
    public void clearDatas() {
        listData.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取数据集
     */
    public ArrayList<T> getDatas() {
        return listData;
    }

    public T getItem(int position) {
        return listData.get(position);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public CommonHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(getItemLayoutId(viewType), parent, false);
        return new CommonHolder((RecyclerView) parent, v);
    }

    @Override
    public void onBindViewHolder(final CommonHolder viewHolder, final int position) {
        final T data = listData.get(position);
        if (itemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long curTime = System.currentTimeMillis();
                    if (curTime - mLastClickTime > minClickIntervaltime) {
                        mLastClickTime = curTime;
                        itemClickListener.onItemClick(viewHolder.itemView, position, data);
                    }
                }
            });
        }

        if (itemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    long curTime = System.currentTimeMillis();
                    if (curTime - mLastClickTime > minClickIntervaltime) {
                        mLastClickTime = curTime;
                        return itemLongClickListener.onItemLongClick(viewHolder.itemView, position, data);
                    }
                    return false;
                }
            });
        }

        setUpData(viewHolder, position, getItemViewType(position), data);
    }

    /**
     * @return 返回<E extends View>
     */
    protected <E extends View> E getView(CommonHolder holder, int id) {
        SparseArray<View> spHolder = holder.spHolder;
        View childView = spHolder.get(id);
        if (null == childView) {
            childView = holder.itemView.findViewById(id);
            spHolder.put(id, childView);
        }
        return (E) childView;
    }

    /**
     * @return 返回<E extends View>
     *
     * @deprecated use {@link #getView(CommonHolder, int)}
     */
    protected <E extends View> E get(CommonHolder holder, int id) {
        return getView(holder, id);
    }

    public static class CommonHolder extends RecyclerView.ViewHolder {
        public SparseArray<View> spHolder = new SparseArray<>();
        public RecyclerView viewParent;

        public CommonHolder(RecyclerView viewParent, View itemView) {
            super(itemView);
            this.viewParent = viewParent;
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View covertView, int position, T data);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View covertView, int position, T data);
    }
}