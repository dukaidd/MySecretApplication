package com.duke.adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.duke.app.MyApplication;
import com.duke.beans.Secret;
import com.duke.beans.User;
import com.duke.secret.ChatActivity;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class APlvAdapter extends BaseAdapter implements OnClickListener {
    private HomeActivity act;
    private List<Secret> secrets;
    private Secret secret;
    private String curent_user;

    public APlvAdapter(HomeActivity act, List<Secret> secrets) {
        super();
        this.act = act;
        this.secrets = secrets;
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
            vh.time1 = (TextView) convertView.findViewById(R.id.item_aplv_time1);
            vh.time2 = (TextView) convertView.findViewById(R.id.item_aplv_time2);
            vh.distance = (TextView) convertView.findViewById(R.id.item_aplv_distance);
            vh.weather = (TextView) convertView.findViewById(R.id.item_aplv_weather);
            vh.sel = (Button) convertView.findViewById(R.id.item_aplv_chat);
            vh.sel.setOnClickListener(this);
            vh.ll = (LinearLayout) convertView.findViewById(R.id.item_aplv_ll);
            vh.like = (Button) convertView.findViewById(R.id.item_aplv_like);
            vh.like.setOnClickListener(this);
            vh.likedNum = (TextView) convertView.findViewById(R.id.item_aplv_likednum);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.text.setText(secrets.get(position).getText());
        vh.text.setTextColor(secrets.get(position).getTextColor());
        vh.ll.setBackgroundColor(secrets.get(position).getBgColor());
        vh.time1.setText(secrets.get(position).getCreatedAt().split(" ")[0]);
        vh.time1.setTextColor(secrets.get(position).getTextColor());
        vh.time2.setText(secrets.get(position).getCreatedAt().split(" ")[1]);
        vh.time2.setTextColor(secrets.get(position).getTextColor());
        vh.distance.setText(getDiatance(position));
        vh.distance.setTextColor(secrets.get(position).getTextColor());
        vh.weather.setText(secrets.get(position).getWeather());
        vh.weather.setTextColor(secrets.get(position).getTextColor());

        if (secrets.get(position).getCollectedUsers() != null
                && secrets.get(position).getCollectedUsers().contains("|" + curent_user + "|")) {
            vh.like.setBackgroundResource(R.drawable.love_p);
        } else {
            vh.like.setBackgroundResource(R.drawable.love_n);
        }
        vh.like.setTag(secrets.get(position));
        vh.sel.setTag(secrets.get(position));
        vh.likedNum.setText(secrets.get(position).getCollectedNum() + "");
        vh.likedNum.setTextColor(secrets.get(position).getTextColor());

        Typeface typeface = Typeface.createFromAsset(act.getAssets(), "fonts/mi.ttf");
        vh.text.setTypeface(typeface);
        vh.time1.setTypeface(typeface);
        vh.time2.setTypeface(typeface);
        vh.distance.setTypeface(typeface);
        vh.weather.setTypeface(typeface);
        vh.likedNum.setTypeface(typeface);
        return convertView;
    }

    private CharSequence getDiatance(int position) {
        if (MyApplication.app.getLocations() != null && MyApplication.app.getLocations().size() != 0) {
            BDLocation location = MyApplication.app.getLocations().get(0);
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
        private TextView text, time1, time2, distance, weather, likedNum;
        private Button sel;
        private Button like;
        private LinearLayout ll;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_aplv_chat:
                secret = (Secret) v.getTag();
                Intent intent = new Intent();
                intent.setClass(act, ChatActivity.class);
                String fromName = secret.getUsername();
                intent.putExtra("name", fromName);
                intent.putExtra("flag", 0);
                act.startActivity(intent);
                break;
            case R.id.item_aplv_like:
                LinearLayout linearLayout = (LinearLayout) v.getParent();
                TextView collectedNum = (TextView) linearLayout.getChildAt(3);
                secret = (Secret) v.getTag();
                if (!(secret == null || secret.equals(""))) {
                    if (secret.getCollectedUsers() == null
                            || !secret.getCollectedUsers().contains("|" + curent_user + "|")) {
                        v.setBackgroundResource(R.drawable.love_p);
                        collectedNum.setText(secret.getCollectedNum() + 1 + "");
                        secret.setCollectedNum(secret.getCollectedNum() + 1);
                        String collectedUser = secret.getCollectedUsers() + "|" + curent_user + "|";
                        secret.setCollectedUsers(collectedUser);
                        secret.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(act, "收藏成功", Toast.LENGTH_SHORT).show();
                                }else{
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
                        secret.update(new UpdateListener() {

                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(act, "取消收藏", Toast.LENGTH_SHORT).show();
                                }else{
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
