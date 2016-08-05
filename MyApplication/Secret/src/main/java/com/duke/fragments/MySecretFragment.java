package com.duke.fragments;

import java.util.List;

import com.duke.adapters.APlvAdapter;
import com.duke.adapters.MPlvAdapter;
import com.duke.base.BaseFragment;
import com.duke.beans.Secret;
import com.duke.beans.User;
import com.duke.secret.HomeActivity;
import com.duke.secret.NewSecretActivity;
import com.duke.secret.R;
import com.duke.utils.StringUtils;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.duke.fragments.AllSecretFragment.mTouchSlop;

public class MySecretFragment extends BaseFragment implements OnClickListener {
	private static final int REQUEST_CODE_1 = 0;
	private HomeActivity act;
	private PullToRefreshListView plv;
	private int count = 1;
	private MPlvAdapter adapter;
	private List<Secret> secrets;
	private ProgressDialog pd;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		act = (HomeActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_mysecret, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		pd = new ProgressDialog(act);
		pd.show();
		initViews();
		super.onActivityCreated(savedInstanceState);
	}

	private void initViews() {
		plv = (PullToRefreshListView) findViewById(R.id.fragment_mysecret_plv);
		ListView listView = plv.getRefreshableView();
		View header = new View(act);
		TypedValue tv = new TypedValue();
		int hight=0;
		if (act.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			hight = TypedValue.complexToDimensionPixelSize(tv.data, act.getResources().getDisplayMetrics());
		}
		header.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,hight));
		header.setBackgroundResource(R.drawable.flag);
		listView.addHeaderView(header);
		listView.setEmptyView(findViewById(R.id.fragment_mysecret_empty));
		listView.setOnTouchListener(myTouchListener);
		plv.setMode(PullToRefreshBase.Mode.BOTH);
		ILoadingLayout startLayout = plv.getLoadingLayoutProxy(true, false);
		ILoadingLayout endLayout = plv.getLoadingLayoutProxy(false, true);
		startLayout.setPullLabel("下拉刷新");
		startLayout.setRefreshingLabel("刷新中...");
		startLayout.setReleaseLabel("释放刷新");
		startLayout.setLastUpdatedLabel(StringUtils.getTime(System.currentTimeMillis()).substring(11,19));
		endLayout.setPullLabel("上拉加载更多");
		endLayout.setRefreshingLabel("正在加载...");
		endLayout.setReleaseLabel("释放加载更多");
		endLayout.setLastUpdatedLabel(StringUtils.getTime(System.currentTimeMillis()).substring(11,19));
		Typeface typeface = Typeface.createFromAsset(act.getAssets(), "fonts/youyuan.ttf");
		startLayout.setTextTypeface(typeface);
		endLayout.setTextTypeface(typeface);
		startLayout.setLoadingDrawable(getResources().getDrawable(R.drawable.ic_wb_sunny_black_24dp));
		endLayout.setLoadingDrawable(getResources().getDrawable(R.drawable.ic_wb_sunny_black_24dp));

		BmobQuery<Secret> query = new BmobQuery<Secret>();
		query.setLimit(10);
		query.order("-createdAt");
		query.addWhereEqualTo("username", BmobUser.getCurrentUser(User.class).getUsername());
		query.findObjects(new FindListener<Secret>() {

			@Override
			public void done(List<Secret> list, BmobException e) {
				if(e==null){
					if (list == null || list.equals("")) {
						return;
					}
					secrets = list;
					adapter = new MPlvAdapter(act, secrets);
					plv.setAdapter(adapter);
					pd.dismiss();
				}else{
					pd.dismiss();
					toast("刷新失败" + e);
				}
			}

		});

		plv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				count = 1;
				BmobQuery<Secret> query = new BmobQuery<Secret>();
				query.setLimit(10);
				query.order("-createdAt");
				query.addWhereEqualTo("username", BmobUser.getCurrentUser(User.class).getUsername());
				query.findObjects(new FindListener<Secret>() {

					@Override
					public void done(List<Secret> list, BmobException e) {
						if(e==null){
							if (list == null || list.equals("")) {
								return;
							}
							secrets = list;
							adapter = new MPlvAdapter(act, secrets);
							plv.setAdapter(adapter);
							plv.onRefreshComplete();
						}else{
							toast("刷新失败");
							plv.onRefreshComplete();
						}
					}
				});
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				BmobQuery<Secret> query = new BmobQuery<Secret>();
				query.setLimit(10 + 5 * count);
				query.order("-createdAt");
				query.addWhereEqualTo("username", BmobUser.getCurrentUser(User.class).getUsername());
				count++;
				query.findObjects(new FindListener<Secret>() {

					@Override
					public void done(List<Secret> list, BmobException e) {
						if(e==null){
							if (list == null || list.equals("")) {
								return;
							}
							if (list.size() > secrets.size()) {
								for (int i = list.size() - secrets.size(); i > 0; i--) {
									secrets.add(list.get(list.size() - i));
								}
							}
							adapter.notifyDataSetChanged();
							plv.onRefreshComplete();
						}else{
							toast("加载失败");
							plv.onRefreshComplete();
						}
					}
				});

			}
		});

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(act, NewSecretActivity.class);
		startActivityForResult(intent, REQUEST_CODE_1);
	}

	@Override
	public void onResume() {
		super.onResume();
		plv.setRefreshing();
	}
	private int direction;
	private float mFirstY;
	private float mCurrentY;
	private boolean mShow = true;
	View.OnTouchListener myTouchListener = new View.OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					mFirstY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					mCurrentY = event.getY();
					if(mCurrentY-mFirstY>mTouchSlop){
						direction = 0;
					}else if(mFirstY-mCurrentY>mTouchSlop){
						direction = 1;
					}
					if(direction ==1){
						if(mShow){
							act.hideAnim(1);
							mShow=!mShow;
						}
					}else if(direction == 0){
						if(!mShow){
							act.hideAnim(0);
							mShow=!mShow;
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					break;
			}
			return false;
		}
	};

	public void scrollToTop() {
		plv.scrollTo(0,0);
	}
	public void refreshListView(){
		adapter.notifyDataSetChanged();
	}

}
