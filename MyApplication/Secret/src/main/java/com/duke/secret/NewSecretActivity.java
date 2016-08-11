package com.duke.secret;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.duke.adapters.NSGvAdapter;
import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.beans.Secret;
import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.easemob.easeui.utils.EaseUserUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.duke.utils.StringUtils.getMonthAndDay;

public class NewSecretActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView weather, username, time;
    private EditText secret;
    private HttpUtils hu;
    private AppCompatImageButton sel;
    private CircleImageView avatar;
    private NSGvAdapter adapter;
    private GridView model;
    private List<Integer> colors;
    private int textColor = Color.BLACK;
//    public static final int bgcolors[] = {R.drawable.color_1, R.drawable.color_2,
//            R.drawable.color_3, R.drawable.color_4, R.drawable.color_5,
//            R.drawable.color_6, R.drawable.color_7, R.drawable.color_8,
//            R.drawable.color_9, R.drawable.color_10};
    public static final int bgcolors[] = {Color.parseColor("#ed5565"), Color.parseColor("#fc6e51"),
            Color.parseColor("#ffce54"), Color.parseColor("#a0d468"), Color.parseColor("#48cfad"),
            Color.parseColor("#4fc1e9"), Color.parseColor("#5d9cec"), Color.parseColor("#ac92ec"),
            Color.parseColor("#ec87c0"), Color.parseColor("#656d78")};
    private int bgColor = bgcolors[2];
    private boolean isEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Slide());
        setContentView(R.layout.activity_new);
        getWindow().setStatusBarColor(Color.parseColor("#C43828"));
        initDatas();
        initViews();
        initViewOper();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                onBackPressed();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.act_newsecret_save);
        initDatas();
        initViews();
        initViewOper();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Secret secret_content = new Secret();
                String secret_text = secret.getText().toString().trim();
                if (secret_text == null || secret_text.equals("")) {
                    Toast.makeText(NewSecretActivity.this, "秘密不能为空", Toast.LENGTH_SHORT);
                    return;
                }
                String weather_content = weather.getText().toString().trim();
                if (weather_content == null || weather_content.equals("")) {
                    weather_content = "霾";
                }
                secret_content.setText(secret.getText().toString().trim());
                secret_content.setTextColor(textColor);
                secret_content.setBgColor(bgColor);
                secret_content.setWeather(weather_content);
                secret_content.setUsername(BmobUser.getCurrentUser(User.class).getUsername());
                BDLocation location = MyApplication.getInstance().getLocations().get(0);
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                secret_content.setLocation(loc);
                secret_content.setAuthor(BmobUser.getCurrentUser(User.class));
                secret_content.save(new SaveListener() {

                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            Toast.makeText(NewSecretActivity.this, "成功新建秘密", Toast.LENGTH_SHORT);
                            MyApplication.getInstance().setSecret(secret_content);
                            finish();
                        } else {
                            Toast.makeText(NewSecretActivity.this, "新建秘密失败", Toast.LENGTH_SHORT);
                        }
                    }

                });


            }
        });
    }

    private void initDatas() {
        colors = new ArrayList<Integer>();
        for (int i = 0; i < bgcolors.length; i++) {
            colors.add(bgcolors[i]);
        }

    }

    private void initViewOper() {
        EaseUserUtils.setUserAvatar(this, BmobUser.getCurrentUser().getUsername(), avatar);
        username.setText(BmobUser.getCurrentUser().getUsername());
        time.setText(getMonthAndDay(System.currentTimeMillis()));
        String city = MyApplication.getInstance().getLocations().get(0).getCity();
        if (city == null) {
            city = "上海";
        } else if (city.equals("")) {
            city = "上海";
        }
        String url = "http://op.juhe.cn/onebox/weather/query?cityname=" + city
                + "&dtype=json&key=dc749e1fd073efce288f3c95258905b2";
        hu.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Toast.makeText(NewSecretActivity.this, "获取天气失败:" + arg1, Toast.LENGTH_SHORT);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                String result = arg0.result;
                String weather_content = parseJson(result);
                weather.setText(weather_content);
            }
        });
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

    private void initViews() {
        hu = new HttpUtils();
        weather = (TextView) findViewById(R.id.act_newsecret_weather);
        avatar = (CircleImageView) findViewById(R.id.act_newsecret_avatar);
        username = (TextView) findViewById(R.id.act_newsecret_username);
        time = (TextView) findViewById(R.id.act_newsecret_time);
        sel = (AppCompatImageButton) findViewById(R.id.act_newsecret_sel);
        model = (GridView) findViewById(R.id.act_newsecret_model);
        sel.setOnClickListener(this);
        adapter = new NSGvAdapter(colors, this);
        model.setAdapter(adapter);
        model.setOnItemClickListener(this);
        secret = (EditText) findViewById(R.id.act_newsecret_secret);
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/mi.ttf");
        weather.setTypeface(typeface);
        username.setTypeface(typeface);
        time.setTypeface(typeface);
        secret.setTypeface(typeface);
    }

    @Override
    public void onClick(View v) {
        isEditable = !isEditable;
        if (isEditable) {
//            sel.setImageResource(R.drawable.favorite_light);
            model.setVisibility(View.VISIBLE);
        } else {
//            sel.setImageResource(R.drawable.favorite_empty);
            model.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // if (position == 9) {
        // secret.setTextColor(Color.WHITE);
        // textColor = Color.WHITE;
        // } else {
        // secret.setTextColor(Color.BLACK);
        // textColor = Color.BLACK;
        // }
        secret.setTextColor(Color.WHITE);
        textColor = Color.WHITE;
        secret.setBackgroundColor(colors.get(position));
        bgColor = colors.get(position);
        sel.setColorFilter(colors.get(position));
    }
}
