package com.duke.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.duke.adapters.MCSFAdapter;
import com.duke.base.BaseFragment;
import com.duke.beans.Secret;
import com.duke.beans.User;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.duke.fragments.AllSecretFragment.mTouchSlop;

public class MyCollectedSecretFragment extends BaseFragment {
    private HomeActivity act;
    private List<Secret> secrets;
    private ListView collections;
    private MCSFAdapter adapter;
    private ProgressDialog pd;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adapter = new MCSFAdapter(secrets, act);
            collections.setAdapter(adapter);
            pd.dismiss();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        act = (HomeActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_mycollectedsecret, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        initDatas();
        initViews();
        super.onActivityCreated(savedInstanceState);
    }

    private void initViews() {
        collections = (ListView) act.findViewById(R.id.fragment_mycollectedsecret_lv);
        collections.setEmptyView(findViewById(R.id.fragment_collection_empty));
        View header = new View(act);
        TypedValue tv = new TypedValue();
        int hight=0;
        if (act.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            hight = TypedValue.complexToDimensionPixelSize(tv.data, act.getResources().getDisplayMetrics());
        }
        header.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,hight));
        header.setBackgroundResource(R.drawable.flag);
        collections.addHeaderView(header);
        collections.setOnTouchListener(myTouchListener);
        pd = new ProgressDialog(act);
        pd.show();
    }

    private void initDatas() {
        BmobQuery<Secret> query = new BmobQuery<Secret>();
        query.setLimit(1000);
        query.order("-createdAt");
        String curent_user = BmobUser.getCurrentUser(User.class).getUsername();
        query.addWhereContains("collectedUsers", "|" + curent_user + "|");
        query.findObjects(new FindListener<Secret>() {

            @Override
            public void done(List<Secret> list, BmobException e) {
                if (e == null) {
                    secrets = list;
                    handler.sendEmptyMessage(1);
                } else {
                    Toast.makeText(act, "获取收藏失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        collections.scrollTo(0,0);
    }
}
