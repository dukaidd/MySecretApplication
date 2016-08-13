package com.duke.secret;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.duke.adapters.CommentLvAdapter;
import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.beans.Comment;
import com.duke.beans.Secret;
import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.duke.utils.StringUtils;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by dukaidd on 2016/8/11.
 */

public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private TextView text, time1, distance, weather, likedNum, username;
    private AppCompatImageButton sel;
    private Button like;
    private FrameLayout fl;
    private CircleImageView avatar;
    private Secret secret;
    private String curent_user;
    private ListView listView;
    private List<Comment> comments;
    private CommentLvAdapter adapter;
    private LinearLayout empty;
    private ImageButton send;
    private AppCompatEditText et;
    private ScrollView sv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#C43828"));
        setContentView(R.layout.activity_comment);
        initView();
        initData();
    }

    private void initView() {
        sv = (ScrollView) findViewById(R.id.activity_comment_sv);
        text = (TextView) findViewById(R.id.item_aplv_text);
        username = (TextView) findViewById(R.id.item_aplv_username);
        time1 = (TextView) findViewById(R.id.item_aplv_time1);
        distance = (TextView) findViewById(R.id.item_aplv_distance);
        weather = (TextView) findViewById(R.id.item_aplv_weather);
        sel = (AppCompatImageButton) findViewById(R.id.item_aplv_chat);
        sel.setOnClickListener(this);
        fl = (FrameLayout) findViewById(R.id.item_aplv_fl);
        like = (Button) findViewById(R.id.item_aplv_like);
        like.setOnClickListener(this);
        likedNum = (TextView) findViewById(R.id.item_aplv_likednum);
        avatar = (CircleImageView) findViewById(R.id.item_aplv_avatar);
        listView = (ListView) findViewById(R.id.activity_comment_lv);
        empty = (LinearLayout) findViewById(R.id.activity_comment_empty);
        listView.setEmptyView(empty);
        et = (AppCompatEditText) findViewById(R.id.activity_comment_et);
        send = (ImageButton) findViewById(R.id.activity_comment_send);
        send.setOnClickListener(this);
    }

    private void initData() {
        curent_user = BmobUser.getCurrentUser().getUsername();
        String objectId = getIntent().getStringExtra("objectId");
        BmobQuery<Secret> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("objectId", objectId);
        query1.findObjects(new FindListener<Secret>() {
            @Override
            public void done(List<Secret> list, BmobException e) {
                if (list == null || list.equals("")) {
                    return;
                }
                secret = list.get(0);
                text.setText(secret.getText());
                text.setTextColor(secret.getTextColor());
                username.setText(StringUtils.getUperCases(secret.getUsername()));
                fl.setBackgroundColor(secret.getBgColor());
                getWindow().setStatusBarColor(secret.getBgColor());
                time1.setText(StringUtils.parseTime(secret.getCreatedAt()));
                distance.setText(getDiatance());
                weather.setText(secret.getWeather());
                EaseUserUtils.setUserAvatar(CommentActivity.this, secret.getUsername(), avatar);
                if (secret.getCollectedUsers() != null
                        && secret.getCollectedUsers().contains("|" + curent_user + "|")) {
                    like.setBackgroundResource(R.drawable.love_p);
                } else {
                    like.setBackgroundResource(R.drawable.love_n);
                }
                likedNum.setText(secret.getCollectedNum() + "");
                Typeface typeface = Typeface.createFromAsset(CommentActivity.this.getAssets(), "fonts/mi.ttf");
                text.setTypeface(typeface);
                username.setTypeface(typeface);
                time1.setTypeface(typeface);
                distance.setTypeface(typeface);
                weather.setTypeface(typeface);
                likedNum.setTypeface(typeface);
                BmobQuery<Comment> query = new BmobQuery<>();
                query.addWhereEqualTo("secret", new BmobPointer(secret));
                query.include("author");
                query.findObjects(new FindListener<Comment>() {
                    @Override
                    public void done(List<Comment> list, BmobException e) {
                        if (e == null) {
                            if (list == null || list.equals("")) {
                                return;
                            } else {
                                comments = list;

                                if (comments == null) {
                                    comments = new ArrayList<>();
                                }

                                adapter = new CommentLvAdapter(comments, CommentActivity.this);
                                listView.setAdapter(adapter);
                                int totalHeight = 0;
                                for (int i = 0; i < adapter.getCount(); i++) {
                                    View listItem = adapter.getView(i, null, listView);
                                    listItem.measure(0, 0);
                                    totalHeight += listItem.getMeasuredHeight();
                                }
                                ViewGroup.LayoutParams params = listView.getLayoutParams();
                                params.height = totalHeight+ (listView.getDividerHeight() * (adapter.getCount() - 1));
                                listView.setLayoutParams(params);
                            }
                        } else {
                            Log.e("duke", e.toString());
                        }
                    }
                });
            }
        });
    }

    private CharSequence getDiatance() {
        if (MyApplication.getInstance().getLocations() != null && MyApplication.getInstance().getLocations().size() != 0) {
            BDLocation location = MyApplication.getInstance().getLocations().get(0);
            LatLng browsedIn = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng createdIn = secret.getLocation();
            long distanceMeters = (long) DistanceUtil.getDistance(browsedIn, createdIn);
            if (distanceMeters > 1000) {
                return "相距" + distanceMeters / 1000 + "公里";
            } else {
                return "相距" + distanceMeters + "米";
            }
        } else {
            return "相距9999米";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_comment_send:
                String content = et.getText().toString();
                if (content == null || content.equals("")) {
                    toast("请输入评论");
                    return;
                }
                et.setText("");
                hideSoftKeyboard();
                User user = BmobUser.getCurrentUser(User.class);
                final Comment comment = new Comment(content, user.getUsername());
                comment.setAuthor(user);
                comment.setSecret(secret);
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            toast("评论成功");
                            comments.add(comment);
                            adapter.notifyDataSetChanged();

                            int totalHeight = 0;
                            for (int i = 0; i < adapter.getCount(); i++) {
                                View listItem = adapter.getView(i, null, listView);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = listView.getLayoutParams();
                            params.height = totalHeight+ (listView.getDividerHeight() * (adapter.getCount() - 1));
                            listView.setLayoutParams(params);
                            sv.post(new Runnable() {
                                public void run() {
                                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
//                            listView.setSelection(comments.size()-1);
                        } else {
                            Log.e("duke", "评论失败" + e);
                        }
                    }
                });
                break;

            case R.id.item_aplv_chat:
                Intent intent = new Intent();
                intent.setClass(this, ChatActivity.class);
                if (secret != null && secret.getUsername() != null) {
                    String fromName = secret.getUsername();
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, fromName);
                    intent.putExtra("flag", 0);
                    this.startActivity(intent);
                }
                break;
            case R.id.item_aplv_like:
                LinearLayout linearLayout = (LinearLayout) v.getParent();
                TextView collectedNum = (TextView) linearLayout.getChildAt(5);
                if (!(secret == null || secret.equals(""))) {
                    if (secret.getCollectedUsers() == null
                            || !secret.getCollectedUsers().contains("|" + curent_user + "|")) {
                        v.setBackgroundResource(R.drawable.love_p);
                        collectedNum.setText(secret.getCollectedNum() + 1 + "");
                        secret.setCollectedNum(secret.getCollectedNum() + 1);
                        String collectedUser = secret.getCollectedUsers() + "|" + curent_user + "|";
                        secret.setCollectedUsers(collectedUser);
                        BmobRelation relation = new BmobRelation();
                        relation.add(BmobUser.getCurrentUser());
                        secret.setLikes(relation);
                        secret.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(CommentActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CommentActivity.this, "收藏失败" + e, Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    } else if (secret.getCollectedUsers() != null
                            && secret.getCollectedUsers().contains("|" + curent_user + "|")) {
                        v.setBackgroundResource(R.drawable.love_n);
                        collectedNum.setText(secret.getCollectedNum() - 1 + "");
                        String collectedUser = secret.getCollectedUsers().replace("|" + curent_user + "|", "");
                        secret.setCollectedUsers(collectedUser);
                        secret.setCollectedNum(secret.getCollectedNum() - 1);
                        BmobRelation relation = new BmobRelation();
                        relation.remove(BmobUser.getCurrentUser());
                        secret.setLikes(relation);
                        secret.update(new UpdateListener() {

                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(CommentActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CommentActivity.this, "取消收藏失败" + e, Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                }
                break;

            default:
                break;
        }
    }
}
