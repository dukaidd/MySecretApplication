package com.duke.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.duke.app.MyApplication;
import com.duke.base.BitmapCache;
import com.duke.beans.Secret;
import com.duke.beans.SecretImage;
import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.duke.secret.CommentActivity;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;
import com.duke.utils.StringUtils;
import com.easemob.easeui.ui.EaseBaiduMapActivity;
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
    private RequestQueue queue;
    private ImageLoader imageLoader;

    public MPlvAdapter(HomeActivity act, List<Secret> secrets) {
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
            convertView = act.getLayoutInflater().inflate(R.layout.item_mplv_adapter, null);
            vh = new ViewHolder();
            vh.username = (TextView) convertView.findViewById(R.id.item_mplv_username);
            vh.image = (NetworkImageView) convertView.findViewById(R.id.item_mplv_image);
            vh.image.setOnClickListener(this);
            vh.text = (TextView) convertView.findViewById(R.id.item_mplv_text);
            vh.text.setOnClickListener(this);
            vh.locsign = (ImageView) convertView.findViewById(R.id.item_mplv_loc);
            vh.locsign.setOnClickListener(this);
            vh.time1 = (TextView) convertView.findViewById(R.id.item_mplv_time1);
            vh.distance = (TextView) convertView.findViewById(R.id.item_mplv_distance);
            vh.distance.setOnClickListener(this);
            vh.weather = (TextView) convertView.findViewById(R.id.item_mplv_weather);
            vh.delete = (AppCompatImageButton) convertView.findViewById(R.id.item_mplv_delete);
            vh.delete.setOnClickListener(this);
            vh.fl = (FrameLayout) convertView.findViewById(R.id.item_mplv_fl);
            vh.like = (Button) convertView.findViewById(R.id.item_mplv_like);
            vh.like.setOnClickListener(this);
            vh.likedNum = (TextView) convertView.findViewById(R.id.item_mplv_likednum);
            vh.avatar = (CircleImageView) convertView.findViewById(R.id.item_mplv_avatar);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Secret secret = secrets.get(position);

        SecretImage secretImage = secret.getImage();

        if (secretImage != null && secretImage.getImage().getUrl() != null) {
            vh.text.setVisibility(View.GONE);
            vh.image.setVisibility(View.VISIBLE);
//            vh.image.setDefaultImageResId(R.drawable.flag);
//            vh.image.setErrorImageResId(R.drawable.flag);
            vh.image.setImageUrl(secretImage.getImage().getUrl(), imageLoader);
        } else {
            vh.image.setVisibility(View.GONE);
            vh.text.setVisibility(View.VISIBLE);
            vh.text.setText(secret.getText());
            vh.text.setTextColor(secret.getTextColor());
        }
        EaseUserUtils.setUserNick(secret.getUsername(),vh.username);
//        vh.username.setText(StringUtils.getUperCases(curent_user));
        vh.fl.setBackgroundColor(secret.getBgColor());
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
        vh.text.setTag(secret);
        vh.delete.setTag(secret);
        vh.locsign.setTag(secret);
        vh.distance.setTag(secret);
        vh.image.setTag(secret);

        vh.likedNum.setText(secret.getCollectedNum() + "");

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
        private TextView text, time1, distance, weather, likedNum, username;
        private AppCompatImageButton delete;
        private Button like;
        private FrameLayout fl;
        private CircleImageView avatar;
        private ImageView locsign;
        private NetworkImageView image;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_mplv_loc:
            case R.id.item_mplv_distance:
                secret = (Secret) v.getTag();
                Intent intent2 = new Intent(act, EaseBaiduMapActivity.class);
                intent2.putExtra("latitude", secret.getLocation().latitude);
                intent2.putExtra("longitude", secret.getLocation().longitude);
                intent2.putExtra("address", "");
                act.startActivity(intent2);
                break;

            case R.id.item_mplv_image:
            case R.id.item_mplv_text:
                Log.e("duke", "textview cliked");
                secret = (Secret) v.getTag();
                Intent intent1 = new Intent(act, CommentActivity.class);
                intent1.putExtra("objectId", secret.getObjectId());
                act.startActivity(intent1);
                break;

            case R.id.item_mplv_delete:
                secret = (Secret) v.getTag();
                new AlertDialog.Builder(act).setTitle("提示").setMessage("确定要删除吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                secret.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(act, "删除成功:" + secret.getUpdatedAt(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(act, "删除失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).show();
                break;
            case R.id.item_mplv_like:
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
