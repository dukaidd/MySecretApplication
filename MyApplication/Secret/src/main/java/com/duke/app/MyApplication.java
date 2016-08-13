package com.duke.app;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.duke.beans.Avatar;
import com.duke.beans.Secret;
import com.duke.beans.User;
import com.duke.secret.ChatActivity;
import com.duke.utils.HXPreferenceUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseCommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.easemob.easeui.utils.EaseCommonUtils.setUserInitialLetter;


public class MyApplication extends Application {
    private static MyApplication instance;
    private Secret secret;

    private Secret secrets_temp;

    private List<BDLocation> locations = new ArrayList<BDLocation>();

    private boolean isOpen;

    public static Map<String, EaseUser> allUsers = new HashMap<>();

    private Map<String, EaseUser> contactList = new HashMap<>();

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }

    public List<BDLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<BDLocation> locations) {

        this.locations = locations;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initBomb();
        initBaidu();

        initHuanxin();
        initNotification();
    }

    private void initHuanxin() {
        EaseUI.getInstance().init(this);
        //get easeui instance
        EaseUI easeUI = EaseUI.getInstance();
        HXPreferenceUtils.init(this);
        //需要easeui库显示用户头像和昵称设置此provider
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getEaseUser(username);
            }
        });
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                return HXPreferenceUtils.getInstance().getSettingMsgNotification();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return HXPreferenceUtils.getInstance().getSettingMsgSound();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return HXPreferenceUtils.getInstance().getSettingMsgVibrate();
            }

            @Override
            public boolean isSpeakerOpened() {
                return HXPreferenceUtils.getInstance().getSettingMsgSpeaker();
            }
        });
    }

    private EaseUser getEaseUser(String username) {
        EaseUser user = null;
        if (allUsers != null) {
            user = allUsers.get(username);
        }
        return user;
    }

    private void initNotification() {
        // TODO Auto-generated method stub
        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 设置notification点击listener
        options.setOnNotificationClickListener(new OnNotificationClickListener() {
            @Override
            public Intent onNotificationClick(EMMessage message) {

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, message.getFrom());
                intent.putExtra("msg", message);
                intent.putExtra("flag", 1);

                return intent;
            }
        });

    }
    private void initBaidu() {

        SDKInitializer.initialize(getApplicationContext());
    }
    private void initBomb() {
        Bmob.initialize(getApplicationContext(), "5c1a8dced4ad68a93a762fef87da2652");
    }
    public void addContacts(List<User> list) {
        for (int i = 0; i < list.size(); i++) {
            final User user = list.get(i);
            final EaseUser easeUser = new EaseUser(user.getUsername());
            if (user.getAvatarUrl() != null) {
                easeUser.setAvatar(user.getAvatarUrl());
            } else {
                BmobQuery<Avatar> query1 = new BmobQuery<Avatar>();
                query1.order("-createdAt");
                query1.addWhereEqualTo("user", user);
                query1.findObjects(new FindListener<Avatar>() {
                    @Override
                    public void done(List<Avatar> list, BmobException e) {
                        if (e == null) {
                            if (list == null || list.equals("")) {
                                return;
                            }
                            user.setAvatarUrl(list.get(0).getAvatar().getUrl());
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Log.i("duke", "user表设置头像URL成功");
                                    }
                                }
                            });
                            if (list.get(0) != null && list.get(0).getAvatar() != null) {
                                String avatarUrl = list.get(0).getAvatar().getUrl();
                                easeUser.setAvatar(avatarUrl);
                            }
                        }
                    }
                });
            }
            if (user.getNick() != null) {
                easeUser.setNick(user.getNick());
            }
            setUserInitialLetter(easeUser);
            if (allUsers == null) {
                allUsers = new HashMap<String, EaseUser>();
            }
            allUsers.put(user.getUsername(), easeUser);
        }
    }

    public void addContactsFromSecrets(List<Secret> list) {
        for (int i = 0; i < list.size(); i++) {
            final User user = list.get(i).getAuthor();
            final EaseUser easeUser = new EaseUser(user.getUsername());
            if (user.getAvatarUrl() != null) {
                easeUser.setAvatar(user.getAvatarUrl());
            } else {
                BmobQuery<Avatar> query1 = new BmobQuery<Avatar>();
                query1.order("-createdAt");
                query1.addWhereEqualTo("user", user);
                query1.findObjects(new FindListener<Avatar>() {
                    @Override
                    public void done(List<Avatar> list, BmobException e) {
                        if (e == null) {
                            if (list == null || list.equals("")) {
                                return;
                            }
                            user.setAvatarUrl(list.get(0).getAvatar().getUrl());
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Log.i("duke", "user表设置头像URL成功");
                                    }
                                }
                            });
                            if (list.get(0) != null && list.get(0).getAvatar() != null) {
                                String avatarUrl = list.get(0).getAvatar().getUrl();
                                easeUser.setAvatar(avatarUrl);
                            }
                        }
                    }
                });
            }
            if (user.getNick() != null) {
                easeUser.setNick(user.getNick());
            }
            setUserInitialLetter(easeUser);
            if (allUsers == null) {
                allUsers = new HashMap<String, EaseUser>();
            }
            allUsers.put(user.getUsername(), easeUser);
        }
    }

    public static MyApplication getInstance() {

        return instance;
    }

    public Secret getSecrets_temp() {
        return secrets_temp;
    }

    public void setSecrets_temp(Secret secrets_temp) {
        instance.secrets_temp = secrets_temp;
    }

    public Map<String, EaseUser> getContactList() {
        if(contactList==null){
            return new HashMap<>();
        }
        return contactList;
    }

    public void setContactList(Map<String, EaseUser> contactList) {
        instance.contactList = contactList;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }



}
