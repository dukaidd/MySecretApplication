package com.duke.adapters;

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
import com.duke.customview.CircleImageView;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;
import com.duke.utils.StringUtils;
import com.easemob.easeui.utils.EaseUserUtils;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class MPlvAdapter extends BaseAdapter implements OnClickListener {
    private HomeActivity act;
    private List<Secret> secrets;
    private Secret secret;
    private String curent_user;

    public MPlvAdapter(HomeActivity act, List<Secret> secrets) {
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
            convertView = act.getLayoutInflater().inflate(R.layout.item_mplv_adapter, null);
            vh = new ViewHolder();
            vh.text = (TextView) convertView.findViewById(R.id.item_mplv_text);
            vh.time1 = (TextView) convertView.findViewById(R.id.item_mplv_time1);
            vh.distance = (TextView) convertView.findViewById(R.id.item_mplv_distance);
            vh.weather = (TextView) convertView.findViewById(R.id.item_mplv_weather);
            vh.delete = (Button) convertView.findViewById(R.id.item_mplv_delete);
            vh.delete.setOnClickListener(this);
            vh.ll = (LinearLayout) convertView.findViewById(R.id.item_mplv_ll);
            vh.like = (Button) convertView.findViewById(R.id.item_mplv_like);
            vh.like.setOnClickListener(this);
            vh.likedNum = (TextView) convertView.findViewById(R.id.item_mplv_likednum);
            vh.avatar = (CircleImageView) convertView.findViewById(R.id.item_mplv_avatar);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Secret secret = secrets.get(position);
        vh.text.setText(secret.getText());
        vh.text.setTextColor(secret.getTextColor());
        vh.ll.setBackgroundColor(secret.getBgColor());
        vh.time1.setText(StringUtils.parseTime(secret.getCreatedAt()));
        vh.distance.setText(getDiatance(position));
        vh.weather.setText(secret.getWeather());
        EaseUserUtils.setUserAvatar(act, secret.getUsername(), vh.avatar);
        if (secret.getCollectedUsers() != null
                && secret.getCollectedUsers().contains("|" + curent_user + "|")) {
            vh.like.setBackgroundResource(R.drawable.love_p);
        } else {
            vh.like.setBackgroundResource(R.drawable.love_n);
        }
        vh.like.setTag(secret);
        vh.delete.setTag(secret);
        vh.likedNum.setText(secret.getCollectedNum() + "");

        Typeface typeface = Typeface.createFromAsset(act.getAssets(), "fonts/mi.ttf");
        vh.text.setTypeface(typeface);
        vh.time1.setTypeface(typeface);
        vh.distance.setTypeface(typeface);
        vh.weather.setTypeface(typeface);
        vh.likedNum.setTypeface(typeface);

        return convertView;
    }

    private CharSequence getDiatance(int position) {
        if (MyApplication.getInstance().getLocations() != null && MyApplication.getInstance().getLocations().size()!=0) {
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
        private TextView text, time1, distance, weather, likedNum;
        private Button delete;
        private Button like;
        private LinearLayout ll;
        private CircleImageView avatar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_mplv_delete:
                secret = (Secret) v.getTag();
                secret.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toast.makeText(act,"删除成功:"+secret.getUpdatedAt(),Toast.LENGTH_SHORT).show();
                            act.msf.refreshListView();
                        }else{
                            Toast.makeText(act,"删除失败：" + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                break;
            case R.id.item_mplv_like:
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
                        BmobRelation relation = new BmobRelation();
                        relation.add(BmobUser.getCurrentUser());
                        secret.setLikes(relation);
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
                        BmobRelation relation = new BmobRelation();
                        relation.remove(BmobUser.getCurrentUser());
                        secret.setLikes(relation);
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
