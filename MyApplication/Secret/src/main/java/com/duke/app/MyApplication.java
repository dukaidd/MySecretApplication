package com.duke.app;

import android.app.Application;
import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.duke.beans.Avatar;
import com.duke.beans.Secret;
import com.duke.beans.User;
import com.duke.secret.ChatActivity;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.easemob.chat.EMChatManager.getInstance;
import static com.easemob.easeui.utils.EaseCommonUtils.setUserInitialLetter;


public class MyApplication extends Application {
    public static MyApplication appInstance;
    private Secret secret;
    private List<BDLocation> locations = new ArrayList<BDLocation>();
    private boolean isOpen;
    public static Map<String, EaseUser> contacts = new HashMap<>();

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

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
        appInstance = this;
        initBomb();
        initBaidu();
        initHuanxin();
        initNotification();
//        initContact();
    }

    private void initHuanxin() {
        EaseUI.getInstance().init(this);
        //get easeui appInstance
        EaseUI easeUI = EaseUI.getInstance();
        //需要easeui库显示用户头像和昵称设置此provider
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getEaseUser(username);
            }
        });
    }
    private EaseUser getEaseUser(String username) {
        EaseUser user = null;
        if(contacts!=null){
            contacts.get(username);
        }
        return user;
    }

    private void initNotification() {
        // TODO Auto-generated method stub
        // 获取到EMChatOptions对象
        EMChatOptions options = getInstance().getChatOptions();
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
    private void initContact() {
        BmobQuery<User> query = new BmobQuery<>();
        query.setLimit(50);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    if(list==null||list.equals("")){
                        return;
                    }
                    addContacts(list);
                }
            }
        });
    }

    public void addContacts(List<User> list) {
        for(int i = 0;i<list.size();i++){
            User user = list.get(i);
            final EaseUser easeUser = new EaseUser(user.getUsername());
            if(user.getAvatarUrl()!=null){
                easeUser.setAvatar(user.getAvatarUrl());
            }else{
                BmobQuery<Avatar> query1 = new BmobQuery<Avatar>();
                query1.addWhereEqualTo("user",user);
                query1.findObjects(new FindListener<Avatar>() {
                    @Override
                    public void done(List<Avatar> list, BmobException e) {
                        if(e==null){
                            if(list==null||list.equals("")){
                                return;
                            }
                            if(list.get(0)!=null&&list.get(0).getAvatar()!=null){
                                String avatarUrl = list.get(0).getAvatar().getUrl();
                                easeUser.setAvatar(avatarUrl);
                            }
                        }
                    }
                });
            }
            if(user.getNick()!=null){
                easeUser.setNick(user.getNick());
            }
            setUserInitialLetter(easeUser);
            if (MyApplication.appInstance.contacts == null) {
                MyApplication.appInstance.contacts = new HashMap<String, EaseUser>();
            }
            MyApplication.getAppInstance().contacts.put(user.getUsername(),easeUser);
        }
    }
    public void addContactsFromSecrets(List<Secret> list) {
        for(int i = 0;i<list.size();i++){
            User user = list.get(i).getAuthor();
            final EaseUser easeUser = new EaseUser(user.getUsername());
            if(user.getAvatarUrl()!=null){
                easeUser.setAvatar(user.getAvatarUrl());
            }else{
                BmobQuery<Avatar> query1 = new BmobQuery<Avatar>();
                query1.addWhereEqualTo("user",user);
                query1.findObjects(new FindListener<Avatar>() {
                    @Override
                    public void done(List<Avatar> list, BmobException e) {
                        if(e==null){
                            if(list==null||list.equals("")){
                                return;
                            }
                            if(list.get(0)!=null&&list.get(0).getAvatar()!=null){
                                String avatarUrl = list.get(0).getAvatar().getUrl();
                                easeUser.setAvatar(avatarUrl);
                            }
                        }
                    }
                });
            }
            if(user.getNick()!=null){
                easeUser.setNick(user.getNick());
            }
            setUserInitialLetter(easeUser);
            if (MyApplication.appInstance.contacts == null) {
                MyApplication.appInstance.contacts = new HashMap<String, EaseUser>();
            }
            MyApplication.getAppInstance().contacts.put(user.getUsername(),easeUser);
        }
    }


    public static MyApplication getAppInstance() {
        return appInstance;
    }
}
