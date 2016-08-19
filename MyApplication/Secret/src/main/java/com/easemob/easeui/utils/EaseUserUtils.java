package com.easemob.easeui.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.util.Util;
import com.duke.app.MyApplication;
import com.duke.beans.Avatar;
import com.duke.beans.User;
import com.duke.secret.R;
import com.duke.utils.StringUtils;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.easemob.easeui.domain.EaseUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.duke.secret.R.id.textView;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * 根据username获取相应user
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(final Context context, String username, final ImageView imageView) {
        final EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //正常的string路径
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_default_male).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        imageView.setImageDrawable(glideDrawable);
                    }
                });
            }
        } else {
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username", username);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        final EaseUser easeUser = new EaseUser(list.get(0).getUsername());
                        if (list.get(0).getNickname() != null) {
                            easeUser.setNick(list.get(0).getNickname());
                        } else {
                            easeUser.setNick(StringUtils.getUperCases(list.get(0).getUsername()));
                        }
                        BmobQuery<Avatar> query = new BmobQuery<Avatar>();
                        query.order("-createdAt");
                        query.addWhereEqualTo("user", list.get(0));
                        query.findObjects(new FindListener<Avatar>() {
                            @Override
                            public void done(List<Avatar> list, BmobException e) {
                                if (e == null) {
                                    easeUser.setAvatar(list.get(0).getAvatar().getUrl());
                                    if(Util.isOnMainThread()){
                                        Glide.with(MyApplication.getInstance()).load(list.get(0).getAvatar().getUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_default_male).into(new SimpleTarget<GlideDrawable>() {
                                            @Override
                                            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                                imageView.setImageDrawable(glideDrawable);
                                            }
                                        });
                                    }

                                    MyApplication.getInstance().allUsers.put(easeUser.getUsername(), easeUser);
                                } else {
                                    Log.e("duke", e.toString());
                                    if(Util.isOnMainThread()){
                                        Glide.with(MyApplication.getInstance()).load(R.drawable.ic_default_male).into(imageView);
                                    }
                                }
                            }
                        });
                    } else {
                        if(Util.isOnMainThread()){
                            Glide.with(MyApplication.getInstance()).load(R.drawable.ic_default_male).into(imageView);
                        }
                        Log.e("duke", e.toString());
                    }
                }
            });
        }
    }

    /**
     * 设置用户昵称
     */
    public static void setUserNick(final String username, final TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereEqualTo("username", username);
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            if (list.get(0).getNickname() != null) {
                                textView.setText(list.get(0).getNickname());
                            } else {
                                textView.setText(StringUtils.getUperCases(username));
                            }
                        } else {
                            Log.e("duke", e.toString());
                            textView.setText(StringUtils.getUperCases(username));
                        }
                    }
                });
            }
        }
    }
    private static String nickname = null;
    public static String getUserNick(final String username) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getNick() != null) {
            return user.getNick();
        } else {
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username", username);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (e == null) {
                        if (list.get(0).getNickname() != null) {
                            nickname = list.get(0).getNickname();
                        } else {
                            nickname = StringUtils.getUperCases(username);
                        }
                    } else {
                        Log.e("duke", e.toString());
                        nickname = StringUtils.getUperCases(username);
                    }
                }
            });
            return nickname;
        }
    }

}
