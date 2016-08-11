package com.duke.service;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.duke.app.MyApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationService extends Service {
	private LocationClient lc;
	private LocationClientOption option;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		lc = new LocationClient(getApplicationContext());
		option = new LocationClientOption();
		option.setCoorType("bd0911");
		option.setIsNeedAddress(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setScanSpan(3000);
		option.setOpenGps(true);
		lc.setLocOption(option);
		lc.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation arg0) {
				// TODO Auto-generated method stub
				if (MyApplication.getInstance().getLocations().size() > 5) {
					MyApplication.getInstance().getLocations().clear();
				}
				MyApplication.getInstance().getLocations().add(arg0);
			}
		});
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		lc.start();
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		lc.stop();
		super.onDestroy();

	}

}
