package com.duke.secret;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.duke.adapters.NSGvAdapter;
import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.base.BitmapCache;
import com.duke.beans.Avatar;
import com.duke.beans.Secret;
import com.duke.beans.SecretImage;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.duke.secret.HomeActivity.REQUEST_CODE;
import static com.duke.utils.StringUtils.getMonthAndDay;

public class NewSecretActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView weather, username, time;
    private EditText secret;
    private HttpUtils hu;
    private AppCompatImageButton sel, pic;
    private ImageView image;
    private CircleImageView avatar;
    private NSGvAdapter adapter;
    private GridView model;
    private List<Integer> colors;
    private int textColor = Color.BLACK;
    public static final int bgcolors[] = {Color.parseColor("#ed5565"), Color.parseColor("#fc6e51"),
            Color.parseColor("#ffce54"), Color.parseColor("#a0d468"), Color.parseColor("#48cfad"),
            Color.parseColor("#4fc1e9"), Color.parseColor("#5d9cec"), Color.parseColor("#ac92ec"),
            Color.parseColor("#ec87c0"), Color.parseColor("#656d78")};
    private int bgColor = bgcolors[2];
    private boolean isEditable;
    private long lastClickTime = 0;
    private RequestQueue queue;
    private ImageLoader imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setEnterTransition(explode);
        Slide slide = new Slide();
        slide.setDuration(500);

        getWindow().setExitTransition(slide);
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
                if(System.currentTimeMillis()-lastClickTime<3000){
                    return;
                }
                lastClickTime = System.currentTimeMillis();

                final Secret secret_content = new Secret();
                String secret_text = secret.getText().toString().trim();
                String weather_content = weather.getText().toString().trim();
                if (weather_content.equals("")) {
                    weather_content = "霾";
                }
                secret_content.setTextColor(textColor);
                secret_content.setBgColor(bgColor);
                secret_content.setWeather(weather_content);
                secret_content.setUsername(BmobUser.getCurrentUser(User.class).getUsername());
                BDLocation location = MyApplication.getInstance().getLocations().get(0);
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                secret_content.setLocation(loc);
                secret_content.setAuthor(BmobUser.getCurrentUser(User.class));

                if (secret.getVisibility() == View.VISIBLE) {
                    if (secret_text.equals("")) {
                        Toast.makeText(NewSecretActivity.this, "分享不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    hideSoftKeyboard();
                    onBackPressed();
                    secret_content.setText(secret_text);
                    secret_content.save(new SaveListener() {

                        @Override
                        public void done(Object o, BmobException e) {
                            if (e == null) {
                                HomeActivity.getInstance().showSuccessToast();
                                hideSoftKeyboard();
                                onBackPressed();
                            } else {
                                HomeActivity.getInstance().showFailToast();
                            }
                        }
                    });
                } else {
                    hideSoftKeyboard();
                    onBackPressed();
                    // Upload
                    String filePath = "/mnt/sdcard/youhu/avatar/image.jpg";
                    BmobFile.uploadBatch(new String[]{filePath}, new UploadBatchListener() {
                        @Override
                        public void onSuccess(List<BmobFile> list, List<String> list1) {
                            SecretImage secretImage = new SecretImage();
                            secretImage.setImage(list.get(0));
                            secretImage.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
//                                                               toast("图片上传成功");
                                        Log.i("duke", s.toString());
                                        BmobQuery<SecretImage> query = new BmobQuery<SecretImage>();
                                        query.addWhereEqualTo("objectId", s);
                                        query.findObjects(new FindListener<SecretImage>() {
                                            @Override
                                            public void done(List<SecretImage> list, BmobException e) {
                                                if (e == null) {

                                                    secret_content.setImage(list.get(0));
                                                    secret_content.save(new SaveListener() {

                                                        @Override
                                                        public void done(Object o, BmobException e) {
                                                            if (e == null) {
                                                                HomeActivity.getInstance().showSuccessToast();
                                                            } else {
                                                                Log.e("duke",e.toString());
                                                                HomeActivity.getInstance().showFailToast();
                                                            }
                                                        }

                                                    });
                                                } else {
                                                    Log.e("duke", e.toString());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("duke", "SecretImage save失败" + e.toString());
                                    }
                                }
                            });
                        }

                        @Override
                        public void onProgress(int i, int i1, int i2, int i3) {
                        }

                        @Override
                        public void onError(int i, String s) {
                            toast("图片上传失败:" + s);
                        }
                    });
                }
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
//        avatar.setDefaultImageResId(R.drawable.ic_default_male);
//        avatar.setErrorImageResId(R.drawable.ic_default_male);
//        BmobQuery<Avatar> query = new BmobQuery<>();
//        query.order("-createdAt");
//        query.addWhereEqualTo("user", BmobUser.getCurrentUser());
//        query.findObjects(new FindListener<Avatar>() {
//            @Override
//            public void done(List<Avatar> list, BmobException e) {
//                if (e == null) {
//                    avatar.setImageUrl(list.get(0).getAvatar().getUrl(), imageLoader);
//                } else {
//                    Log.e("duke", e.toString());
//                }
//            }
//        });

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
        queue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(queue, new BitmapCache());
        hu = new HttpUtils();
        pic = (AppCompatImageButton) findViewById(R.id.act_newsecret_pic);
        pic.setOnClickListener(this);
        image = (ImageView) findViewById(R.id.act_newscret_image);
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
        switch (v.getId()) {
            case R.id.act_newsecret_pic:
                createDialog();
                break;

            case R.id.act_newsecret_sel:
                closeOrOpenPalette();
                break;
        }
    }

    public void closeOrOpenPalette() {
        isEditable = !isEditable;
        if (isEditable) {
            model.setVisibility(View.VISIBLE);
        } else {
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
        closeOrOpenPalette();
        secret.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);
        secret.setTextColor(Color.WHITE);
        textColor = Color.WHITE;
        secret.setBackgroundColor(colors.get(position));
        bgColor = colors.get(position);
        sel.setColorFilter(colors.get(position));
    }

    //--------------设置头像方法开始-----------------------------------------------------------------------------------

    private void createDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("选择方式");
        dialog.setIcon(R.drawable.ic_collections_black_24dp);
        dialog.setItems(new String[]{"本地相册", "相机拍照"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (Build.VERSION.SDK_INT < 19) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CODE);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.OPEN_DOCUMENT");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_CODE);
                    }

                } else if (which == 1) {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
        dialog.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Bitmap mbit = null;
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri == null && data.hasExtra("data")) {
                    mbit = (Bitmap) data.getExtras().get("data");
                    saveBitmap(mbit);
                    mbit = compressImage(mbit);
                    secret.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(mbit);
                    secret.setBackgroundColor(Color.parseColor("#00000000"));
                } else {
                    try {
                        mbit = MediaStore.Images.Media.getBitmap(NewSecretActivity.this.getContentResolver(), uri);
                        mbit = compressImage(mbit);
                        secret.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                        image.setImageBitmap(mbit);
                        secret.setBackgroundColor(Color.parseColor("#00000000"));
                        saveBitmap(mbit);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 60) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public void saveBitmap(Bitmap bitmap) {
        File dir = new File("/mnt/sdcard/youhu/avatar");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("/mnt/sdcard/youhu/avatar/image.jpg");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    }

    //--------------设置头像方法结束------------------------------------------------------------------------------------
}
