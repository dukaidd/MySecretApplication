package com.duke.secret;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.duke.utils.StringUtils;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseUserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by dukaidd on 2016/8/19.
 */

public class FriendMsgActivity extends BaseActivity implements View.OnClickListener {
    private TextView nickname, username, slogan;
    private CircleImageView avatar;
    private ImageView gender;
    private Button btn_sendmsg, btn_add;
    private User user;
    private Toolbar toolbar;
    private HomeActivity homeAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendmsg);
        homeAct = HomeActivity.getInstance();
        initView();
        setUpView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.activity_friendmsg_bar);
        toolbar.setTitle("详细信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nickname = (TextView) findViewById(R.id.tv_name);
        username = (TextView) findViewById(R.id.tv_accout);
        slogan = (TextView) findViewById(R.id.tv_sign);
        avatar = (CircleImageView) findViewById(R.id.iv_avatar);
        gender = (ImageView) findViewById(R.id.iv_sex);
        btn_sendmsg = (Button) findViewById(R.id.btn_sendmsg);
        nickname.setTypeface(homeAct.typeface);
        username.setTypeface(homeAct.typeface);
        slogan.setTypeface(homeAct.typeface);
        btn_add = (Button) findViewById(R.id.btn_add_contact);
    }

    private void setUpView() {
        String author = getIntent().getStringExtra("author");
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", author);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    user = list.get(0);
                    if (MyApplication.getInstance().getContactList().get(user.getUsername()) != null) {
                        btn_add.setVisibility(View.GONE);
                    }
                    EaseUserUtils.setUserNick(user.getUsername(), nickname);
                    EaseUserUtils.setUserAvatar(FriendMsgActivity.this, user.getUsername(), avatar);
                    if (user.getSex().equals("男")) {
                        gender.setImageResource(R.drawable.ic_sex_male);
                    } else {
                        gender.setImageResource(R.drawable.ic_sex_female);
                    }
                    username.setText("用户名：" + user.getUsername());
                    slogan.setText(user.getSlogan());
                } else {
                    Log.e("duke", e.toString());
                }
            }
        });

        btn_sendmsg.setOnClickListener(this);
        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sendmsg:
                Intent intent = new Intent(this, ChatActivity.class);
                if (user.getUsername() != null) {
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
                    intent.putExtra("flag", 0);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.btn_add_contact:
                User currentUser = BmobUser.getCurrentUser(User.class);
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                currentUser.setFriends_relation(relation);
                currentUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Map<String, EaseUser> contacts = MyApplication.getInstance().getContactList();
                            if (contacts == null) {
                                contacts = new HashMap<>();
                            }
                            EaseUser easeUser = new EaseUser(user.getUsername());
                            if (user.getNickname() != null) {
                                easeUser.setNick(user.getNickname());
                            } else {
                                easeUser.setNick(StringUtils.getUperCases(user.getUsername()));
                            }
                            contacts.put(user.getUsername(), easeUser);
                            MyApplication.getInstance().setContactList(contacts);
                            HomeActivity.getInstance().refreshContactList();
                            btn_add.setVisibility(View.GONE);
                            Toast.makeText(FriendMsgActivity.this, "成功添加为联系人", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FriendMsgActivity.this, "添加联系人失败" + e, Toast.LENGTH_SHORT).show();
                            Log.e("duke", "更新relation失败" + e);
                        }
                    }
                });
                break;
        }
    }
}
