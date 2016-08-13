package com.duke.secret;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.duke.base.BaseActivity;
import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.duke.service.LocationService;
import com.duke.utils.BitmapUtil;
import com.duke.utils.StringUtils;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.duke.utils.StringUtils.getMonthAndDay;


public class SplashActivity extends BaseActivity implements AnimationListener, DialogInterface.OnClickListener {
    private ImageView imageView;
    private AlphaAnimation animation;
    private ScaleAnimation scaleAnimation;
    private TranslateAnimation translateAnimation;
    private TextView time, weather,username;
    private LocationClient lc;
    private LocationClientOption option;
    private CircleImageView avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Explode explode = new Explode();
        explode.setDuration(1000);
        getWindow().setEnterTransition(explode);
        Slide slide = new Slide();
        slide.setDuration(1000);

        setContentView(R.layout.act_splash);
        getWindow().setStatusBarColor(Color.parseColor("#C43828"));

        startService(new Intent(this, LocationService.class));
        isFirstLaunch();
        logined();
        initViews();
        initLocation();
    }

    private void initLocation() {
        lc = new LocationClient(getApplicationContext());
        option = new LocationClientOption();
        option.setCoorType("bd0911");
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(3000);
        option.setOpenGps(true);
        lc.setLocOption(option);
        lc.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation arg0) {
                // TODO Auto-generated method stub
                String city = arg0.getCity();

                if (city == null) {
                    city = "上海";
                } else if (city.equals("")) {
                    city = "上海";
                }
                String url = "http://op.juhe.cn/onebox/weather/query?cityname=" + city
                        + "&dtype=json&key=dc749e1fd073efce288f3c95258905b2";
                HttpUtils hu = new HttpUtils();
                hu.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        String result = arg0.result;
                        String weather_content = parseJson(result);
                        weather.setText(weather_content);
                    }
                });

            }
        });
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
        if (BmobUser.getCurrentUser(User.class) != null && EMChat.getInstance().isLoggedIn()) {
            // ** 免登陆情况 加载所有本地群和会话
            //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
            //加上的话保证进了主页面会话和群组都已经load完毕
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();
            username = (TextView) findViewById(R.id.splash_username);
            username.setText(StringUtils.getUperCases(BmobUser.getCurrentUser().getUsername()));
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username",BmobUser.getCurrentUser().getUsername());
            query.include("avatar_pointer");
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e==null){
                        String avatarUrl = list.get(0).getAvatarUrl();
                        BitmapUtil.getBitUtil(SplashActivity.this).display(avatar,avatarUrl);
                    }else{
                        Log.e("duke","Splash"+e);

                    }
                }
            });



        }
    }

    private void initViews() {
        avatar = (CircleImageView) findViewById(R.id.splash_avatar);
        username = (TextView) findViewById(R.id.splash_username);
        time = (TextView) findViewById(R.id.splash_time);
        weather = (TextView) findViewById(R.id.splash_weather);
        imageView = (ImageView) findViewById(R.id.splash_image);
        animation = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.splash_alpha);
        scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(this, R.anim.splash_scale);

        imageView.setAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(this);
        time.setText(getMonthAndDay(System.currentTimeMillis()));
    }

    private String parseJson(String result) {
        String weather_content = null;
        try {
            JSONObject jo = new JSONObject(result);
            JSONObject jo2 = jo.getJSONObject("result");
            JSONObject jo3 = jo2.getJSONObject("data");
            JSONObject jo4 = jo3.getJSONObject("realtime");
            JSONObject jo5 = jo4.getJSONObject("weather");
            weather_content = jo5.getString("info");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return weather_content;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, "没有可用网络", Toast.LENGTH_SHORT).show();
            dialog("提示", "没有网络，前去设置！", R.drawable.ease_msg_state_fail_resend,
                    "设置", "取消", this, this);
        } else {
            if (BmobUser.getCurrentUser(User.class) != null && EMChat.getInstance().isLoggedIn()) {
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

    @Override
    protected void onStart() {
        lc.start();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        lc.stop();
        super.onDestroy();
    }
}
