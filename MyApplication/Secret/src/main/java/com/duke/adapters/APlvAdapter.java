package com.duke.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.duke.app.MyApplication;
import com.duke.base.BitmapCache;
import com.duke.beans.Secret;
import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.duke.secret.ChatActivity;
import com.duke.secret.CommentActivity;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;
import com.duke.utils.StringUtils;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.utils.EaseUserUtils;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class APlvAdapter extends BaseAdapter implements OnClickListener {
    private HomeActivity act;
    private List<Secret> secrets;
    private Secret secret;
    private String curent_user;
    private RequestQueue queue;
    private ImageLoader imageLoader;
    public APlvAdapter(HomeActivity act, List<Secret> secrets) {
        super();
        this.act = act;
        this.secrets = secrets;
        queue = Volley.newRequestQueue(act);
        imageLoader = new ImageLoader(queue, new BitmapCache());
        curent_user = BmobUser.getCurrentUser(User.class).getUsername();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return secrets.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return secrets.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = act.getLayoutInflater().inflate(R.layout.item_aplv_adapter, null);
            vh = new ViewHolder();
            vh.text = (TextView) convertView.findViewById(R.id.item_aplv_text);
            vh.text.setOnClickListener(this);
            vh.username = (TextView) convertView.findViewById(R.id.item_aplv_username);
            vh.time1 = (TextView) convertView.findViewById(R.id.item_aplv_time1);
            vh.distance = (TextView) convertView.findViewById(R.id.item_aplv_distance);
            vh.weather = (TextView) convertView.findViewById(R.id.item_aplv_weather);
            vh.sel = (AppCompatImageButton) convertView.findViewById(R.id.item_aplv_chat);
            vh.sel.setOnClickListener(this);
            vh.fl = (FrameLayout) convertView.findViewById(R.id.item_aplv_fl);
            vh.fl.setOnClickListener(this);
            vh.like = (Button) convertView.findViewById(R.id.item_aplv_like);
            vh.like.setOnClickListener(this);
            vh.likedNum = (TextView) convertView.findViewById(R.id.item_aplv_likednum);
            vh.avatar = (CircleImageView) convertView.findViewById(R.id.item_aplv_avatar);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Secret secret = secrets.get(position);
        vh.text.setText(secret.getText());
        vh.text.setTextColor(secret.getTextColor());
        vh.username.setText(StringUtils.getUperCases(secret.getUsername()));
        vh.fl.setBackgroundColor(secret.getBgColor());
        vh.time1.setText(StringUtils.parseTime(secret.getCreatedAt()));
        vh.distance.setText(getDiatance(position));
        vh.weather.setText(secret.getWeather());
//        final String imgUrl = secret.getAvatarUrl();
//            if (imgUrl != null && !imgUrl.equals("")) {
//                BitmapUtil.getBitUtil(act).display(vh.avatar,imgUrl);
//            }else{
//                BmobQuery<Avatar> query = new BmobQuery<>();
//                query.addWhereEqualTo("username",secret.getUsername());
//                query.order("-createdAt");
//                final ViewHolder finalVh = vh;
//                query.findObjects(new FindListener<Avatar>() {
//                    @Override
//                    public void done(List<Avatar> list, BmobException e) {
//                        if(e==null){
//                            if(list==null||list.equals("")){
//                                finalVh.avatar.setImageResource(R.drawable.ic_default_male);
//                            }else if(list.get(0).getAvatar()!=null){
//                                secret.setAvatarUrl(list.get(0).getAvatar().getUrl());
//                                BitmapUtil.getBitUtil(act).display(finalVh.avatar,list.get(0).getAvatar().getUrl());
//                                secret.update(new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        if(e == null){
//                                            Log.i("duke",secret.getAvatarUrl());
//                                        }
//                                    }
//                                });
//                            }else{
//                                finalVh.avatar.setImageResource(R.drawable.ic_default_male);
//                            }
//                        }else{
//                            finalVh.avatar.setImageResource(R.drawable.ic_default_male);
//                        }
//                    }
//                });
//
//            }

        EaseUserUtils.setUserAvatar(act, secret.getUsername(), vh.avatar);
        if (secret.getCollectedUsers() != null
                && secret.getCollectedUsers().contains("|" + curent_user + "|")) {
            vh.like.setBackgroundResource(R.drawable.love_p);
        } else {
            vh.like.setBackgroundResource(R.drawable.love_n);
        }
        vh.likedNum.setText(secret.getCollectedNum()+"");

        vh.like.setTag(secret);
        vh.sel.setTag(secret);
        vh.text.setTag(secret);

        Typeface typeface = Typeface.createFromAsset(act.getAssets(), "fonts/mi.ttf");
        vh.text.setTypeface(typeface);
        vh.username.setTypeface(typeface);
        vh.time1.setTypeface(typeface);
        vh.distance.setTypeface(typeface);
        vh.weather.setTypeface(typeface);
        vh.likedNum.setTypeface(typeface);
        return convertView;
    }

    private CharSequence getDiatance(int position) {
        if (MyApplication.getInstance().getLocations() != null && MyApplication.getInstance().getLocations().size() != 0) {
            BDLocation location = MyApplication.getInstance().getLocations().get(0);
            LatLng browsedIn = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng createdIn = secrets.get(position).getLocation();
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

    static class ViewHolder {
        private TextView text, time1, distance, weather, likedNum,username;
        private AppCompatImageButton sel;
        private Button like;
        private FrameLayout fl;
        private CircleImageView avatar;
        private ImageView image;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_aplv_text:
                Log.e("duke","textview cliked");
                secret = (Secret) v.getTag();
                Intent intent1 = new Intent(act, CommentActivity.class);
                intent1.putExtra("objectId",secret.getObjectId());
                act.startActivity(intent1);
                break;


            case R.id.item_aplv_chat:
                secret = (Secret) v.getTag();
                Intent intent = new Intent();
                intent.setClass(act, ChatActivity.class);
                String fromName = secret.getUsername();
                if(fromName!=null){
                    intent.putExtra(EaseConstant.EXTRA_USER_ID, fromName);
                    intent.putExtra("flag", 0);
                    act.startActivity(intent);
                }
                break;
            case R.id.item_aplv_like:
                LinearLayout linearLayout = (LinearLayout) v.getParent();
                TextView collectedNum = (TextView) linearLayout.getChildAt(5);
                secret = (Secret) v.getTag();
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
                                    Toast.makeText(act, "收藏成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(act, "收藏失败" + e, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(act, "取消收藏", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(act, "取消收藏失败" + e, Toast.LENGTH_SHORT).show();
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
