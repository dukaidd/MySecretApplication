package com.duke.secret;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.duke.adapters.CommentLvAdapter;
import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.base.BitmapCache;
import com.duke.beans.Comment;
import com.duke.beans.Secret;
import com.duke.beans.SecretImage;
import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.duke.utils.StringUtils;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.ui.EaseBaiduMapActivity;
import com.easemob.easeui.utils.EaseUserUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private TextView text, time1, distance, weather, likedNum, nickname;
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
    private ImageView locsign;
    private NetworkImageView image;
    private RequestQueue queue;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#C43828"));
        setContentView(R.layout.activity_comment);
        queue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(queue, new BitmapCache());
        initView();
        initData();
    }

    private void initView() {
        image = (NetworkImageView) findViewById(R.id.item_aplv_image);
        locsign = (ImageView) findViewById(R.id.item_aplv_loc);
        locsign.setOnClickListener(this);
        sv = (ScrollView) findViewById(R.id.activity_comment_sv);
        text = (TextView) findViewById(R.id.item_aplv_text);
        nickname = (TextView) findViewById(R.id.item_aplv_nickname);
        nickname.setOnClickListener(this);
        time1 = (TextView) findViewById(R.id.item_aplv_time1);
        time1.setOnClickListener(this);
        distance = (TextView) findViewById(R.id.item_aplv_distance);
        distance.setOnClickListener(this);
        weather = (TextView) findViewById(R.id.item_aplv_weather);
        weather.setOnClickListener(this);
        sel = (AppCompatImageButton) findViewById(R.id.item_aplv_chat);
        sel.setOnClickListener(this);
        fl = (FrameLayout) findViewById(R.id.item_aplv_fl);
        like = (Button) findViewById(R.id.item_aplv_like);
        like.setOnClickListener(this);
        likedNum = (TextView) findViewById(R.id.item_aplv_likednum);
        avatar = (CircleImageView) findViewById(R.id.item_aplv_avatar);
        avatar.setOnClickListener(this);
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
        query1.include("image");
        query1.addWhereEqualTo("objectId", objectId);
        query1.findObjects(new FindListener<Secret>() {
            @Override
            public void done(List<Secret> list, BmobException e) {
                if (list == null || list.equals("")) {
                    return;
                }
                secret = list.get(0);
                SecretImage secretImage = secret.getImage();
                if (secretImage != null && secretImage.getImage().getUrl() != null) {
                    text.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
//                    image.setDefaultImageResId(R.drawable.flag);
//                    image.setErrorImageResId(R.drawable.flag);
                    image.setImageUrl(secretImage.getImage().getUrl(), imageLoader);
                    setStatusBarColor(secretImage.getImage().getUrl());


                } else {
                    image.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                    text.setText(secret.getText());
                    text.setTextColor(secret.getTextColor());
                    getWindow().setStatusBarColor(secret.getBgColor());
                }

                EaseUserUtils.setUserNick(secret.getUsername(), nickname);
                fl.setBackgroundColor(secret.getBgColor());
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
                nickname.setTypeface(typeface);
                time1.setTypeface(typeface);
                distance.setTypeface(typeface);
                weather.setTypeface(typeface);
                likedNum.setTypeface(typeface);
                BmobQuery<Comment> query1 = new BmobQuery<>();
                query1.addWhereEqualTo("secret", new BmobPointer(secret));
                query1.include("author");
                query1.findObjects(new FindListener<Comment>() {
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
                                params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
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

    private void setStatusBarColor(final String avatarUrl) {
        if (avatarUrl != null) {
            new AsyncTask<String, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap mbitmap = null;
                    URL fileUrl = null;
                    try {
                        fileUrl = new URL(avatarUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try {
                        HttpURLConnection conn = (HttpURLConnection) fileUrl
                                .openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        mbitmap = BitmapFactory.decodeStream(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mbitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        // get muted color from bitmap using palette and set this to collapsible toolbar
                        @Override
                        public void onGenerated(Palette palette) {
                            // 通过Palette 来获取对应的色调
                            Palette.Swatch vibrant =
                                    palette.getDarkMutedSwatch();
                            // 将颜色设置给相应的组件
                            if (vibrant != null) {
                                getWindow().setStatusBarColor(vibrant.getRgb());
                            }
                        }
                    });
                }
            }.execute();
        }
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
            case R.id.item_aplv_weather:
            case R.id.item_aplv_time1:
            case R.id.item_aplv_nickname:
            case R.id.item_aplv_avatar:
                if(secret.getUsername()!=null){
                    Intent intent3 = new Intent(this, FriendMsgActivity.class);
                    intent3.putExtra("author",secret.getUsername());
                    startActivity(intent3);
                }
                break;

            case R.id.item_aplv_loc:
            case R.id.item_aplv_distance:
                Intent intent2 = new Intent(this, EaseBaiduMapActivity.class);
                intent2.putExtra("latitude", secret.getLocation().latitude);
                intent2.putExtra("longitude", secret.getLocation().longitude);
                intent2.putExtra("address", "");
                this.startActivity(intent2);
                break;

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
                            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
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
