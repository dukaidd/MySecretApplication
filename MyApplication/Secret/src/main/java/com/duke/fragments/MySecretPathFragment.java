package com.duke.fragments;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.duke.app.MyApplication;
import com.duke.base.BaseFragment;
import com.duke.beans.Secret;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MySecretPathFragment extends BaseFragment implements OnMarkerClickListener {
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private List<LatLng> locations;
	private List<Secret> secrets;
	private List<Marker> mMarker;
	private HomeActivity act;
	ProgressDialog pd;
	int[] icons = { R.drawable.icon_marka, R.drawable.icon_markb, R.drawable.icon_markc, R.drawable.icon_markd,
			R.drawable.icon_marke };

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		act = (HomeActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_mysecretpath, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initViews();
		getLocations();
		super.onActivityCreated(savedInstanceState);
	}

	private void getLocations() {
		locations = new ArrayList<LatLng>();
		BmobQuery<Secret> query = new BmobQuery<Secret>();
		query.setLimit(5);
		query.order("-createdAt");
		query.addWhereEqualTo("username", BmobUser.getCurrentUser().getUsername());
		query.findObjects(new FindListener<Secret>() {

			@Override
			public void done(List<Secret> list, BmobException e) {
				if(e==null){
					secrets = list;
					if (!(list == null || list.equals(""))) {
						for (int i = 0; i < list.size(); i++) {
							locations.add(list.get(i).getLocation());
						}
						addMarkers();
						pd.dismiss();
					}
				}else{
						toast("最新秘密获取失败:" + e);
				}
			}
		});

	}

	private void addMarkers() {
		mMarker = new ArrayList<Marker>();
		for (int j = 0; j < locations.size(); j++) {
			BitmapDescriptor ic = BitmapDescriptorFactory.fromResource(icons[j]);
			OverlayOptions options = new MarkerOptions().position(locations.get(j)).icon(ic);
			mMarker.add((Marker) mBaiduMap.addOverlay(options));
		}

	}

	private void initViews() {
		// TODO Auto-generated method stub
		mMapView = (MapView) findViewById(R.id.fragment_mysecretpath_mv);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMarkerClickListener(this);
		pd = new ProgressDialog(act);
		pd.show();
		LatLng pos = new LatLng(MyApplication.appInstance.getLocations().get(0).getLatitude(),
				MyApplication.appInstance.getLocations().get(0).getLongitude());

		BitmapDescriptor ic = BitmapDescriptorFactory.fromResource(R.drawable.ov_dot_blue);
		OverlayOptions options = new MarkerOptions().position(pos).icon(ic);
		mBaiduMap.addOverlay(options);

		MapStatus ms = new MapStatus.Builder().target(pos).zoom(15).build();
		MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.setMapStatus(msu);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onResume时执行mMapView. onPause (),实现地图生命周期管理
		mMapView.onPause();
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		for (int i = 0; i < mMarker.size(); i++) {
			if (mMarker.get(i) == arg0) {
				Button button = new Button(act);
				button.setBackgroundResource(R.drawable.map_pop_bg_1);
				String secret_text = secrets.get(i).getText();
				String bt_text = "";
				if (secret_text.length() > 10) {
					bt_text = secret_text.substring(0, 10);
				} else {
					bt_text = secret_text;
				}
				bt_text = bt_text + "\n" + secrets.get(i).getCreatedAt().substring(0,10);
				button.setText(bt_text);
				final InfoWindow infoWindow = new InfoWindow(button, locations.get(i), -70);
				mBaiduMap.showInfoWindow(infoWindow);
			}
		}
		return false;
	}
}
