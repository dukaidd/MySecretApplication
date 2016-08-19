package com.duke.secret;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.beans.Avatar;
import com.duke.beans.User;
import com.duke.customview.CircleImageView;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseUserUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.duke.secret.HomeActivity.REQUEST_CODE;

/**
 * Created by dukaidd on 2016/8/19.
 */

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private CircleImageView avatar;
    private EditText nickname, slogan;
    private Button confirm;
    private HomeActivity homeAct;
    private Toolbar toolbar;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_userinfo);
        toolbar = (Toolbar) findViewById(R.id.activity_userinfo_bar);
        toolbar.setTitle("个人信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                onBackPressed();
            }
        });
        homeAct = HomeActivity.getInstance();
        initView();
        setUpView();
    }

    private void initView() {
        user = BmobUser.getCurrentUser(User.class);
        avatar = (CircleImageView) findViewById(R.id.head);
        nickname = (EditText) findViewById(R.id.edit_name);
        slogan = (EditText) findViewById(R.id.slogan);
        confirm = (Button) findViewById(R.id.btn_start);
        nickname.setTypeface(homeAct.typeface);
        slogan.setTypeface(homeAct.typeface);

    }

    private void setUpView() {
        EaseUserUtils.setUserAvatar(this, user.getUsername(), avatar);
        EaseUserUtils.setUserNick(user.getUsername(), nickname);
        avatar.setOnClickListener(this);
        confirm.setOnClickListener(this);
        if (user.getSlogan() != null) {
            slogan.setText(user.getSlogan());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
                createDialog();

                break;
            case R.id.btn_start:
                hideSoftKeyboard();
                onBackPressed();
                user.setNickname(nickname.getText().toString());
                MyApplication.allUsers.get(user.getUsername()).setNick(nickname.getText().toString());
                user.setSlogan(slogan.getText().toString());
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            homeAct.settingRefresh();
                            homeAct.setNaviNick();
                            homeAct.showSetSuccessToast();
                        } else {
                            homeAct.showSetFailToast();
                            Log.e("duke", e.toString());
                        }
                    }
                });
                break;
        }

    }
    //--------------设置头像方法开始-----------------------------------------------------------------------------------

    private void createDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("选择方式");
        dialog.setIcon(R.drawable.ic_account_box_black_24dp);
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
                    avatar.setImageBitmap(mbit);
                    homeAct.setAvatarAndBG(mbit);
                } else {
                    try {
                        mbit = MediaStore.Images.Media.getBitmap(UserInfoActivity.this.getContentResolver(), uri);
                        mbit = compressImage(mbit);
                        avatar.setImageBitmap(mbit);
                        homeAct.setAvatarAndBG(mbit);
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
        while (baos.toByteArray().length / 1024 > 20) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
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
            fos = new FileOutputStream("/mnt/sdcard/youhu/avatar/avatar.jpg");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        // Upload
        String filePath = "/mnt/sdcard/youhu/avatar/avatar.jpg";
        BmobFile.uploadBatch(new String[]{filePath}, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
//                toast("上传成功");
                final User user = BmobUser.getCurrentUser(User.class);

                final Avatar avatar = new Avatar();
                avatar.setUsername(user.getUsername());
                avatar.setUser(user);
                avatar.setAvatar(list.get(0));
                avatar.save(new SaveListener() {

                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            toast("设置成功");
                            Log.i("duke", o.toString());
                            BmobQuery<Avatar> query = new BmobQuery<Avatar>();
                            query.addWhereEqualTo("objectId", o);
                            query.findObjects(new FindListener<Avatar>() {
                                @Override
                                public void done(List<Avatar> list, BmobException e) {
                                    if (e == null) {
                                        MyApplication.allUsers.get(user.getUsername()).setAvatar(list.get(0).getAvatar().getUrl());
                                        homeAct.settingRefresh();
                                    } else {
                                        Log.i("duke", list.toString());
                                    }
                                }
                            });

                        } else {
                            toast("设置失败" + e);
                        }
                    }
                });

            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
            }

            @Override
            public void onError(int i, String s) {
                toast("上传失败:" + s);
            }
        });
    }

    //--------------设置头像方法结束------------------------------------------------------------------------------------

}
