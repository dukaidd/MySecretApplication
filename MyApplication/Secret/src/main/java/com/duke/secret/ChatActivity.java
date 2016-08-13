package com.duke.secret;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.beans.User;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.ui.EaseChatFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    private String toChatUsername;
    public static final int CHATTYPE_SINGLE = 1;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_chat_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                onBackPressed();
            }
        });
        activityInstance = this;
        //聊天人或群id
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
//        Log.i("duke", "onCreate: "+toChatUsername);
        if (toChatUsername != null) {
            toolbar.setSubtitle(toChatUsername);
        }
        chatFragment = new EaseChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.activity_menu_add) {
            final User user = BmobUser.getCurrentUser(User.class);
            if (toChatUsername.equals(user.getUsername())) {
                Toast.makeText(ChatActivity.this, "不能添加自己", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            } else if (MyApplication.getInstance().getContactList() != null && MyApplication.getInstance().getContactList().get(toChatUsername) != null) {
                Toast.makeText(ChatActivity.this, "您的通讯录中有此联系人", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username", toChatUsername);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        if (list == null || list.equals("")) {
                            Log.e("duke", "查找无此人");
                            return;
                        }
                        if (list.get(0) != null) {
                            BmobRelation relation = new BmobRelation();
                            final User toChatUser = list.get(0);
                            relation.add(toChatUser);
                            user.setFriends_relation(relation);
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Map<String, EaseUser> contacts = MyApplication.getInstance().getContactList();
                                        if (contacts == null) {
                                            contacts = new HashMap<>();
                                        }
                                        EaseUser easeUser = new EaseUser(toChatUser.getUsername());
                                        if (toChatUser.getAvatarUrl() != null) {
                                            easeUser.setAvatar(toChatUser.getAvatarUrl());
                                        }
                                        if (toChatUser.getNick() != null) {
                                            easeUser.setNick(toChatUser.getNick());
                                        }
                                        contacts.put(toChatUser.getUsername(), easeUser);
                                        MyApplication.getInstance().setContactList(contacts);
                                        HomeActivity.getInstance().refreshContactList();
                                        Toast.makeText(ChatActivity.this, "成功添加为联系人", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChatActivity.this, "添加联系人失败" + e, Toast.LENGTH_SHORT).show();
                                        Log.e("duke", "更新relation失败" + e);
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("duke", "查找toChatUsername失败：" + e);
                        Toast.makeText(ChatActivity.this, "该账户已被移除", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else if (id == R.id.activity_menu_clear)

        {
            chatFragment.emptyHistory();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }

    public String getToChatUsername() {
        return toChatUsername;
    }
}
