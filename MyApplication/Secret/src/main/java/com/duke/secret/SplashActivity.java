package com.duke.secret;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.beans.Avatar;
import com.duke.beans.User;
import com.duke.service.LocationService;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseCommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class SplashActivity extends BaseActivity implements AnimationListener, DialogInterface.OnClickListener {
    private ImageView imageView;
    private AlphaAnimation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        getWindow().setStatusBarColor(Color.parseColor("#C43828"));
        isFirstLaunch();
        logined();
        initViews();
        startService(new Intent(this, LocationService.class));
    }

    private void isFirstLaunch() {
        boolean isFirst = true;
        isFirst = getSharedPreferences("times", Context.MODE_PRIVATE).getBoolean("isFirst", true);
        if (isFirst) {
            startActivity(new Intent(this, MyGuideActivity.class));
            finish();
        }
    }

    private void logined() {
        if (BmobUser.getCurrentUser(User.class) != null&& EMChat.getInstance().isLoggedIn()) {
            // ** 免登陆情况 加载所有本地群和会话
            //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
            //加上的话保证进了主页面会话和群组都已经load完毕
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();
        }

    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.splash_iv);
        animation = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.splash_alpha);
        imageView.setAnimation(animation);
        animation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, "没有可用网络", Toast.LENGTH_SHORT).show();
            dialog("提示", "没有网络，前去设置！", android.R.drawable.ic_dialog_alert,
                    "设置", "取消", this, this);
        }else {
            if (BmobUser.getCurrentUser(User.class) != null&& EMChat.getInstance().isLoggedIn()) {
                startActivity(new Intent(this, HomeActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            this.finish();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

//	public boolean isNetConnected() {
//		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo nInfo = cManager.getActiveNetworkInfo();
//		if (nInfo == null) {
//			return false;
//		} else {
//			return nInfo.isAvailable();
//		}
//	}

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON1) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_SETTINGS);
            startActivity(intent);
            this.finish();
        } else {
            this.finish();
        }
    }
}
