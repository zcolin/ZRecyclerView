ZRecyclerViewLib
=
### RecyclerView的下拉刷新到底加载的库，使用android自带的SwipeRefreshLayout封装。同我的另外一个库[PullRecyclerView](https://github.com/zcolin/PullRecyclerView)的接口基本一样，可以做到无缝切换

Feature
=
1. 制定自定义样式加载Footer请实现ILoadMoreFooter，参照DefLoadMoreFooter.
2. 可以设置HeaderView、FooterView、emptypView、下拉样式、加载样式等操作.
3. 所有设置在ZRecyclerView中操作，不再在Adapter中进行操作.
4. 支持滑动菜单.
5. 解决android SwipeRefreshLayout和AppBarLayout冲突的问题.

Demo
=
![](screenshot/1.gif)


## Gradle
app的build.gradle中添加
```
dependencies {
    compile 'com.github.zcolin:ZRecyclerView:latest.release'
}
```
工程的build.gradle中添加
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

usage
=

```
//默认已设置LinearLayoutManager
recyclerView.setGridLayout(true, 2);

//设置刷新和加载更多回调
recyclerView.setOnPullLoadMoreListener(new ZRecyclerView.PullLoadMoreListener() {
    @Override
    public void onRefresh() {
        
    }

    @Override
    public void onLoadMore() {

    }
});

//设置数据为空时的EmptyView，DataObserver是注册在adapter之上的，也就是setAdapter是注册上，notifyDataSetChanged的时候才会生效
recyclerView.setEmptyView(this, R.layout.view_recycler_empty);

//设置HeaderView和footerView
recyclerView.addHeaderView(this, R.layout.view_recyclerheader);
recyclerView.addFooterView(this, R.layout.view_recyclerfooter);
// recyclerView.removeHeaderView(View)

//设置加载更多进度条样式
recyclerView.setLoadMoreProgressStyle(ProgressStyle.LineScaleIndicator);

//设置加载进度条View,此处可以设置 https://github.com/81813780/AVLoadingIndicatorView 的view
recyclerView.setLoadMoreProgressView(view);

//设置Item监听
recyclerView.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<String>() {
    @Override
    public void onItemClick(View covertView, int position, String data) {
        Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT)
             .show();
    }
});

//所有数据加载完毕后，不显示已加载全部
recyclerView.setIsShowNoMore(false);

//到底加载是否可用
recyclerView.setIsLoadMoreEnabled(false);

//下拉刷新是否可用
recyclerView.setIsRefreshEnabled(false);

//处理与子控件的冲突，如viewpager
recyclerView.setIsProceeConflict(true);   

//设置自定义的加载Footer
recyclerView.setLoadMoreFooter(customview implements ILoadMoreFooter);  

//设置加载文字
recyclerView.setLoadMoreText("正在加载...", "正在加载...", "*****已加载全部*****");

//增加默认分割线
recyclerView.addDefaultItemDecoration();

//有下拉刷新效果，手动调用刷新数据
recyclerView.refreshWithPull();

//没有下拉刷新效果，直接刷新数据
recyclerView.refresh();

//只有下拉刷新效果，不刷新数据
recyclerView.setRefreshing(true);
```




