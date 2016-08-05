package com.duke.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.duke.adapters.SCLvAdapter;
import com.duke.base.BaseFragment;
import com.duke.secret.ChatActivity;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import static com.duke.fragments.AllSecretFragment.mTouchSlop;

public class SecretChatFragment extends BaseFragment implements OnItemClickListener {
    private List<EMConversation> conversations;
    private SCLvAdapter adapter;
    private ListView lv;
    private HomeActivity act;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        act = (HomeActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.frament_chatlist, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        initDatas();
        initViews();
        super.onActivityCreated(savedInstanceState);
    }

    private void initViews() {
        // TODO Auto-generated method stub
        lv = (ListView) findViewById(R.id.fragment_chatlist_lv);
        lv.setEmptyView(findViewById(R.id.fragment_chatlist_empty));
        lv.setOnItemClickListener(this);
        View header = new View(act);
        TypedValue tv = new TypedValue();
        int hight = 0;
        if (act.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            hight = TypedValue.complexToDimensionPixelSize(tv.data, act.getResources().getDisplayMetrics());
        }
        header.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, hight));
        header.setBackgroundResource(R.drawable.flag);
        lv.addHeaderView(header);
        lv.setOnTouchListener(myTouchListener);
        adapter = new SCLvAdapter(act, conversations);
        lv.setAdapter(adapter);
    }

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
                            act.hideAnim(1);
                            mShow = !mShow;
                        }
                    } else if (direction == 0) {
                        if (!mShow) {
                            act.hideAnim(0);
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

    private void initDatas() {
        // TODO Auto-generated method stub
        Hashtable<String, EMConversation> hashtable = EMChatManager.getInstance().getAllConversations();
        Collection<EMConversation> c = hashtable.values();
        conversations = new ArrayList<EMConversation>();
        for (Iterator iterator = c.iterator(); iterator.hasNext(); ) {
            EMConversation emConversation = (EMConversation) iterator.next();
            conversations.add(emConversation);
        }
    }

    // ************************************************************************
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = conversations.get(position).getUserName();
        Intent intent = new Intent();
        intent.setClass(act, ChatActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("flag", 0);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new SCLvAdapter(act, conversations);
        lv.setAdapter(adapter);
    }
}
