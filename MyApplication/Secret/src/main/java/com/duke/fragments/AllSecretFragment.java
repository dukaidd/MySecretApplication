package com.duke.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.duke.adapters.APlvAdapter;
import com.duke.app.MyApplication;
import com.duke.base.BaseFragment;
import com.duke.beans.Secret;
import com.duke.secret.HomeActivity;
import com.duke.secret.NewSecretActivity;
import com.duke.secret.R;
import com.duke.utils.StringUtils;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AllSecretFragment extends BaseFragment {
    private HomeActivity act;
    private PullToRefreshListView plv;
    private List<Secret> secrets;
    private APlvAdapter adapter;
    public static int REQUEST_CODE_2 = 2;
    private ProgressDialog pd;
    private int count = 1;
    private ListView listView;


    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        act = (HomeActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        pd = new ProgressDialog(act);
        pd.setMessage("正在获取数据...");
        pd.show();
        return inflater.inflate(R.layout.fragment_allsecret, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        initViews();
        initDatas();
        super.onActivityCreated(savedInstanceState);
    }

    private void initDatas() {
        BmobQuery<Secret> query = new BmobQuery<Secret>();
        query.setLimit(10);
        query.include("author");
        query.order("-createdAt");
        query.findObjects(new FindListener<Secret>() {

            @Override
            public void done(List<Secret> list, BmobException e) {
                if (e == null) {
                    pd.dismiss();
                    if (list == null) {
                        secrets = new ArrayList<Secret>();
                    } else {
                        secrets = list;
                    }
                    MyApplication.getInstance().addContactsFromSecrets(list);
                    adapter = new APlvAdapter(act, secrets);
                    plv.setAdapter(adapter);
                    if (secrets.size() == 0) {
                        dialog("提示", "还没有知识,前去分享", android.R.drawable.ic_dialog_alert, "创建", "取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivityForResult(new Intent(act, NewSecretActivity.class), REQUEST_CODE_2);
                                    }
                                }, null);
                    }
                } else {
                    pd.dismiss();
                    toast("获取知识失败:" + e);
                    Log.i("duke", e.toString());
                }
            }
        });


    }

    private void initViews() {
        plv = (PullToRefreshListView) findViewById(R.id.fragment_allsecret_plv);
        listView = plv.getRefreshableView();
        View header = new View(act);
        TypedValue tv = new TypedValue();
        int hight = 0;
        if (act.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            hight = TypedValue.complexToDimensionPixelSize(tv.data, act.getResources().getDisplayMetrics());
        }
        header.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, hight));
        header.setBackgroundResource(R.drawable.flag);
        listView.addHeaderView(header);
        listView.setOnTouchListener(myTouchListener);
        plv.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLayout = plv.getLoadingLayoutProxy(true, false);
        ILoadingLayout endLayout = plv.getLoadingLayoutProxy(false, true);
        startLayout.setPullLabel("下拉刷新");
        startLayout.setRefreshingLabel("刷新中...");
        startLayout.setReleaseLabel("释放刷新");
        startLayout.setLastUpdatedLabel(StringUtils.getTime(System.currentTimeMillis()).substring(11, 16));
        endLayout.setPullLabel("上拉加载更多");
        endLayout.setRefreshingLabel("正在加载...");
        endLayout.setReleaseLabel("释放加载更多");
        Typeface typeface = Typeface.createFromAsset(act.getAssets(), "fonts/mi.ttf");
        startLayout.setTextTypeface(typeface);
        endLayout.setTextTypeface(typeface);
        startLayout.setLoadingDrawable(getResources().getDrawable(R.drawable.ic_wb_sunny_black_24dp));
        endLayout.setLoadingDrawable(getResources().getDrawable(R.drawable.ic_wb_sunny_black_24dp));
        endLayout.setLastUpdatedLabel(StringUtils.getTime(System.currentTimeMillis()).substring(11, 16));
        plv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                count = 1;
                BmobQuery<Secret> query = new BmobQuery<Secret>();
                query.setLimit(20);
                query.include("author");
                query.order("-createdAt");
                query.findObjects(new FindListener<Secret>() {

                    @Override
                    public void done(List<Secret> list, BmobException e) {
                        if (e == null) {
                            if (list == null || list.equals("")) {
                                return;
                            }
                            secrets = list;
                            MyApplication.getInstance().addContactsFromSecrets(list);
                            adapter = new APlvAdapter(act, secrets);
                            plv.setAdapter(adapter);
                            plv.onRefreshComplete();
                        } else {
                            //停止刷新动画
                            plv.onRefreshComplete();
                            Log.e("duke", e.toString());
                            toast("刷新失败" + e);
                        }
                    }
                });
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                BmobQuery<Secret> query = new BmobQuery<Secret>();
                query.setLimit(10 + 5 * count);
                query.include("author");
                query.order("-createdAt");
                count++;
                query.findObjects(new FindListener<Secret>() {

                    @Override
                    public void done(List<Secret> list, BmobException e) {
                        if (e == null) {
                            if (list == null || list.equals("")) {
                                return;
                            }
                            if (list.size() > secrets.size()) {
                                for (int i = list.size() - secrets.size(); i > 0; i--) {
                                    secrets.add(list.get(list.size() - i));
                                }
                            }
                            MyApplication.getInstance().addContactsFromSecrets(list);
                            adapter.notifyDataSetChanged();
                            plv.onRefreshComplete();
                        } else {
                            plv.onRefreshComplete();
                            toast("加载失败" + e);
                            Log.i("duke", e.toString());
                        }
                    }
                });
            }

        });
    }

    public static final int mTouchSlop = 10;
    private int direction;
    private float mFirstY;
    private float mCurrentY;
    private boolean mShow = true;
    View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mFirstY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurrentY = event.getY();
                    if (mCurrentY - mFirstY > mTouchSlop) {
                        direction = 0;
                    } else if (mFirstY - mCurrentY > mTouchSlop) {
                        direction = 1;
                    }
                    if (direction == 1) {
                        if (mShow) {
                            act.hideToolbarAndFb(1);
                            act.hideBottomBar(1);
                            mShow = !mShow;
                        }
                    } else if (direction == 0) {
                        if (!mShow) {
                            act.hideToolbarAndFb(0);
                            act.hideBottomBar(0);
                            mShow = !mShow;
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
        listView.smoothScrollToPosition(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSoftKeyboard();
    }

}
