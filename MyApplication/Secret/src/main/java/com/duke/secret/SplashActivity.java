package com.duke.secret;

import com.duke.base.BaseActivity;
import com.duke.beans.User;
import com.duke.service.LocationService;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;

public class SplashActivity extends BaseActivity implements AnimationListener, DialogInterface.OnClickListener {
	private ImageView imageView;
	private AlphaAnimation animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_splash);
		getWindow().setStatusBarColor(Color.parseColor("#C43828"));
		initViews();
		startService(new Intent(this, LocationService.class));
	}

	private void initViews() {
		imageView = (ImageView) findViewById(R.id.splash_logo);
		animation = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.splash_alpha);
		imageView.setAnimation(animation);
		animation.setAnimationListener(this);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (!isNetConnected()) {
			Toast.makeText(this, "没有可用网络", Toast.LENGTH_SHORT).show();
			 dialog("提示", "没有网络，前去设置！", android.R.drawable.ic_dialog_alert,
			 "设置", "取消", this, this);
		}

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (BmobUser.getCurrentUser(User.class) != null) {
			startActivity(new Intent(this, HomeActivity.class));
		} else {
			startActivity(new Intent(this, LoginActivity.class));
		}
		this.finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	public boolean isNetConnected() {
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cManager.getActiveNetworkInfo();
		if (nInfo == null) {
			return false;
		} else {
			return nInfo.isAvailable();
		}
	}

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
