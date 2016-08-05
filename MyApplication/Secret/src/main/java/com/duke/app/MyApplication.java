package com.duke.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.duke.beans.Secret;
import com.duke.secret.ChatActivity;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnNotificationClickListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.Bmob;

import static com.easemob.chat.EMChatManager.getInstance;


public class MyApplication extends Application {
    public static MyApplication app;
    private Secret secret;
    private List<BDLocation> locations = new ArrayList<BDLocation>();
    private boolean isOpen;

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
        initBomb();
        initBaidu();
        initHuanXin();
        initNotification();
        app = this;
        super.onCreate();
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
                intent.putExtra("msg", message);
                intent.putExtra("flag", 1);
                return intent;
            }
        });

    }


    private void initHuanXin() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
        // name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase("com.duke.secret")) {
            Log.e("dk", "enter the service process!");
            // "com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        EMChat.getInstance().init(getApplicationContext());
        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         *
         * @param debugMode
         *            在做代码混淆的时候需要设置成false
         */
        EMChat.getInstance().setDebugMode(true);// 在做打包混淆时，要关闭debug模式，避免消耗不必要的资源
        EMChatOptions chatOptions = getInstance().getChatOptions();
        chatOptions.setNoticedByVibrate(false);
    }

    private void initBaidu() {

        SDKInitializer.initialize(getApplicationContext());
    }

    private void initBomb() {
        Bmob.initialize(getApplicationContext(), "5c1a8dced4ad68a93a762fef87da2652");
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
