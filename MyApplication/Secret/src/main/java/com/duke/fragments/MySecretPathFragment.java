package com.duke.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.duke.app.MyApplication;
import com.duke.base.BaseFragment;
import com.duke.beans.Secret;
import com.duke.secret.HomeActivity;
import com.duke.secret.R;
import com.duke.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
    private List<Integer> colors;
    private ProgressDialog pd;
    int[] icons = {R.drawable.icon_marka, R.drawable.icon_markb, R.drawable.icon_markc, R.drawable.icon_markd,
            R.drawable.icon_marke};

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
                if (e == null) {
                    secrets = list;
                    if (!(list == null || list.equals(""))) {
                        for (int i = 0; i < list.size(); i++) {
                            locations.add(list.get(i).getLocation());
                        }
                        addMarkers();
                        pd.dismiss();
                    }
                } else {
                    pd.dismiss();
//                    toast("您的分享少于两个");
                    Log.e("duke",e.toString());
                }
            }
        });

        colors = new ArrayList<>();
        colors.add(R.color.orange);
        colors.add(R.color.colorPrimary);
        colors.add(R.color.common_top_bar_blue);
        colors.add(R.color.btn_gray_pressed);

    }

    private void addMarkers() {
        mMarker = new ArrayList<Marker>();
        for (int j = 0; j < locations.size(); j++) {
            BitmapDescriptor ic = BitmapDescriptorFactory.fromResource(icons[j]);
            OverlayOptions options = new MarkerOptions().position(locations.get(j)).icon(ic);
            mMarker.add((Marker) mBaiduMap.addOverlay(options));
        }
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .colorsValues(colors).points(locations);
        //添加在地图中
        Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
    }

    private void initViews() {
        // TODO Auto-generated method stub
        mMapView = (MapView) findViewById(R.id.fragment_mysecretpath_mv);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(this);
        pd = new ProgressDialog(act);
        pd.setMessage("正在获取数据...");
        pd.show();
        LatLng pos = new LatLng(MyApplication.getInstance().getLocations().get(0).getLatitude(),
                MyApplication.getInstance().getLocations().get(0).getLongitude());

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
                TextView textView = new TextView(act);
                textView.setBackgroundResource(R.drawable.map_pop_bg_1);
                textView.setPadding(30, 20, 30, 20);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Typeface typeface = Typeface.createFromAsset(act.getAssets(), "fonts/mi.ttf");
                textView.setTypeface(typeface);
                textView.setGravity(Gravity.CENTER);
                String secret_text = secrets.get(i).getText();
                String bt_text = "";
                if (secret_text == null) {
                    bt_text = "[图片]";
                } else if (secret_text.length() > 10) {
                    bt_text = secret_text.substring(0, 10);
                } else {
                    bt_text = secret_text;
                }
                bt_text = bt_text + "\n" + StringUtils.parseTime(secrets.get(i).getCreatedAt());
                textView.setText(bt_text);
                final InfoWindow infoWindow = new InfoWindow(textView, locations.get(i), -70);
                mBaiduMap.showInfoWindow(infoWindow);
            }
        }
        return false;
    }
}
