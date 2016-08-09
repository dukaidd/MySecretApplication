package com.duke.secret;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duke.app.MyApplication;
import com.duke.base.BaseActivity;
import com.duke.beans.Avatar;
import com.duke.beans.User;
import com.duke.fragments.AllSecretFragment;
import com.duke.fragments.MyCollectedSecretFragment;
import com.duke.fragments.MySecretFragment;
import com.duke.fragments.MySecretPathFragment;
import com.duke.fragments.SettingsFragment;
import com.duke.utils.BitmapUtil;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.EaseConstant;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.ui.EaseContactListFragment;
import com.easemob.easeui.ui.EaseConversationListFragment;
import com.easemob.easeui.utils.EaseCommonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static com.duke.app.MyApplication.getAppInstance;


public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private final int SDK_PERMISSION_REQUEST = 127;
    public static final int REQUEST_CODE = 0;
    private String permissionInfo;
    private FloatingActionButton fab;
    public MySecretFragment msf;
    private AllSecretFragment af;
    private MyCollectedSecretFragment mcsf;
    private MySecretPathFragment mspf;
    private ImageView nav_avatar;
    private TextView nav_username, nav_email;
    private NavigationView navigationView;
    private LinearLayout navigationHeader;
    private Toolbar toolbar;
    private ObjectAnimator Animator_toolbar;
    private ObjectAnimator Animator_bottom;

    private AppBarLayout home_bar;
    private NewMessageBroadcastReceiver1 msgReceiver;
    private TextView unreadLabel;
    private Button[] mTabs;
    private EaseConversationListFragment conversationListFragment;
    private EaseContactListFragment contactListFragment;
    private SettingsFragment settingFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex = 0;
    private LinearLayout ll_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getPersimmions();
        initView();
        initFragment();

        msgReceiver = new NewMessageBroadcastReceiver1();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
        EMChat.getInstance().setAppInited();

    }

    private void initView() {
        getWindow().setStatusBarColor(Color.parseColor("#C43828"));

        home_bar = (AppBarLayout) findViewById(R.id.home_app_bar);

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.home_new_secret);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ll_bottom = (LinearLayout) findViewById(R.id.main_bottom);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationHeader = (LinearLayout) navigationView.getHeaderView(0);

        nav_avatar = (ImageView) navigationHeader.getChildAt(0);
        nav_username = (TextView) navigationHeader.getChildAt(1);
        nav_email = (TextView) navigationHeader.getChildAt(2);

        nav_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
        User user = BmobUser.getCurrentUser(User.class);
        setAvatarAndBG(user);
        nav_username.setText(user.getUsername().toString());
        nav_email.setText(user.getEmail().toString());

        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
    }

    private void setAvatarAndBG(Bitmap bitmap) {
        nav_avatar.setImageBitmap(bitmap);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            // get muted color from bitmap using palette and set this to collapsible toolbar
            @Override
            public void onGenerated(Palette palette) {
                // 通过Palette 来获取对应的色调
                Palette.Swatch vibrant =
                        palette.getDarkMutedSwatch();
                // 将颜色设置给相应的组件
                if (vibrant != null) {
                    navigationHeader.setBackgroundColor(vibrant.getRgb());
                }
            }
        });
    }

    private void setAvatarAndBG(final User user) {
        if (user.getAvatarUrl() != null) {
            final String avatarUrl = user.getAvatarUrl();
            BitmapUtil.getBitUtil(HomeActivity.this).display(nav_avatar, avatarUrl);
            new AsyncTask<String, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap mbitmap = null;
                    URL fileUrl = null;
                    try {
                        fileUrl = new URL(avatarUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try {
                        HttpURLConnection conn = (HttpURLConnection) fileUrl
                                .openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        mbitmap = BitmapFactory.decodeStream(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mbitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        // get muted color from bitmap using palette and set this to collapsible toolbar
                        @Override
                        public void onGenerated(Palette palette) {
                            // 通过Palette 来获取对应的色调
                            Palette.Swatch vibrant =
                                    palette.getDarkMutedSwatch();
                            // 将颜色设置给相应的组件
                            if (vibrant != null) {
                                navigationHeader.setBackgroundColor(vibrant.getRgb());
                            }
                        }
                    });
                }
            }.execute();
        }else{
            BmobQuery<Avatar> query = new BmobQuery<Avatar>();
            query.addWhereEqualTo("user", user);
            query.findObjects(new FindListener<Avatar>() {
                @Override
                public void done(List<Avatar> list, BmobException e) {
                    if (e == null) {
                        if (list == null || list.equals("")) {
                            toast("null");
                            return;
                        }
                        if(list.get(0)!=null&&list.get(0).getAvatar()!=null){

                            setAvatarAndBG(list.get(0).getAvatar().getUrl());
                            user.setAvatarUrl(list.get(0).getAvatar().getUrl());
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Log.i("duke","user表设置头像URL成功");
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void setAvatarAndBG(final String avatarUrl) {
        if (avatarUrl != null) {
            BitmapUtil.getBitUtil(HomeActivity.this).display(nav_avatar, avatarUrl);
            new AsyncTask<String, Integer, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap mbitmap = null;
                    URL fileUrl = null;
                    try {
                        fileUrl = new URL(avatarUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try {
                        HttpURLConnection conn = (HttpURLConnection) fileUrl
                                .openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        mbitmap = BitmapFactory.decodeStream(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mbitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        // get muted color from bitmap using palette and set this to collapsible toolbar
                        @Override
                        public void onGenerated(Palette palette) {
                            // 通过Palette 来获取对应的色调
                            Palette.Swatch vibrant =
                                    palette.getDarkMutedSwatch();
                            // 将颜色设置给相应的组件
                            if (vibrant != null) {
                                navigationHeader.setBackgroundColor(vibrant.getRgb());
                            }
                        }
                    });
                }
            }.execute();
        }
    }

    private void initFragment() {
        af = new AllSecretFragment();
        msf = new MySecretFragment();
        mspf = new MySecretPathFragment();
        mcsf = new MyCollectedSecretFragment();

        mTabs = new Button[4];
        mTabs[0] = (Button) findViewById(R.id.btn_discovery);
        mTabs[1] = (Button) findViewById(R.id.btn_conversation);
        mTabs[2] = (Button) findViewById(R.id.btn_address_list);
        mTabs[3] = (Button) findViewById(R.id.btn_setting);
        // set first tab as selected
        mTabs[0].setSelected(true);

        conversationListFragment = new EaseConversationListFragment();

        contactListFragment = new EaseContactListFragment();
        settingFragment = new SettingsFragment();
        contactListFragment.setContactsMap(getContacts());
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(HomeActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
            }
        });
        contactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {

            @Override
            public void onListItemClicked(EaseUser user) {
                startActivity(new Intent(HomeActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
            }
        });
        fragments = new Fragment[]{af, conversationListFragment, contactListFragment, settingFragment, msf, mspf, mcsf};
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, af).
                add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(af)
                .commit();
    }

    private long current;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (Math.abs(System.currentTimeMillis() - current) < 2000) {
                HomeActivity.this.finish();
            } else {
                current = System.currentTimeMillis();
//                Snackbar.make(ll_bottom, "", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                toast("再次点击返回退出");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_discovery) {
            hideToolbarAndFb(0);
            hideBottomBar(0);
            index = 0;
        } else if (id == R.id.nav_my_secret) {
            hideToolbarAndFb(0);
            hideBottomBar(0);
            index = 4;
        } else if (id == R.id.nav_my_secret_path) {
            hideToolbarAndFb(0);
            hideBottomBar(0);
            index = 5;
        } else if (id == R.id.nav_my_collection) {
            hideToolbarAndFb(0);
            hideBottomBar(0);
            index = 6;
        } else if (id == R.id.nav_quit) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            BmobUser.logOut();
            EMChatManager.getInstance().logout();
            getAppInstance().contacts = null;
            HomeActivity.this.finish();
        }

        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        for (int j = 0; j < 4; j++) {
            mTabs[j].setSelected(false);
        }
        if (index < 4 && currentTabIndex < 4) {
            mTabs[currentTabIndex].setSelected(false);
            // set current tab as selected.
            mTabs[index].setSelected(true);
        } else if (index < 4 && currentTabIndex > 4) {
            // set current tab as selected.
            mTabs[index].setSelected(true);
        } else if (index > 4 && currentTabIndex < 4) {
            mTabs[currentTabIndex].setSelected(false);
            // set current tab as selected.
        } else if (index > 4 && currentTabIndex > 4) {

        }


        currentTabIndex = index;


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_new_secret:
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, NewSecretActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, fab, "float_action_button").toBundle());
                break;
            case R.id.toolbar_home:
//                initContact();
                if (index == 0) {
                    if (Math.abs(System.currentTimeMillis() - current) < 2000) {
                        af.scrollToTop();
                    } else {
                        current = System.currentTimeMillis();
//                        Snackbar.make(ll_bottom, , Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                        toast("双击标题栏回到顶部");
                    }
                }
                break;
        }
    }

    private class NewMessageBroadcastReceiver1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 消息id
            String msgId = intent.getStringExtra("msgid");
            // 发消息的人的username(userid)
            String msgFrom = intent.getStringExtra("from");
            // 消息类型，文本，图片，语音消息等,这里返回的值为msg.type.ordinal()。
            // 所以消息type实际为是enum类型
            int msgType = intent.getIntExtra("type", 0);
            Log.d("duke", "new message id:" + msgId + " from:" + msgFrom + " type:" + msgType);
            // 更方便的方法是通过msgId直接获取整个message
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            if (!MyApplication.appInstance.isOpen()) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                //API level 11
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle(message.getFrom());
                builder.setContentText(((TextMessageBody) message.getBody()).getMessage());
                builder.setSmallIcon(R.drawable.flower);
                builder.setAutoCancel(true);
                builder.setWhen(System.currentTimeMillis());

                intent.setClass(HomeActivity.this, ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, msgFrom);
                intent.putExtra("flag", 1);
                PendingIntent contentIntent = PendingIntent.getActivity(HomeActivity.this, 0, intent, 0);
                builder.setContentIntent(contentIntent);

                Notification notification = builder.getNotification();

                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notification.defaults = Notification.DEFAULT_ALL;

                manager.notify(R.drawable.flower, notification);
            }
        }
    }
    //-------获取运行时权限-----------------------------------------------------------------
    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }

            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            if (addPermission(permissions, Manifest.permission.WRITE_SETTINGS)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
//             读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    //-------------------------------------------------------------------------------------------------
    private void createDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("选择方式");
        dialog.setIcon(android.R.drawable.ic_input_add);
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
                    mbit = compressImage(mbit);
                    setAvatarAndBG(mbit);
                    saveBitmap(mbit);
                } else {
                    try {
                        mbit = MediaStore.Images.Media.getBitmap(HomeActivity.this.getContentResolver(), uri);
                        mbit = compressImage(mbit);
                        setAvatarAndBG(mbit);
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
        while (baos.toByteArray().length / 1024 > 4) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public void saveBitmap(Bitmap bitmap) {
        File dir = new File("/mnt/sdcard/secret/avatar");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("/mnt/sdcard/secret/avatar/avatar.jpg");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        // Upload
        String filePath = "/mnt/sdcard/secret/avatar/avatar.jpg";
        BmobFile.uploadBatch(new String[]{filePath}, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
//                toast("上传成功");
                User user = BmobUser.getCurrentUser(User.class);

                Avatar avatar = new Avatar();
                avatar.setUsername(user.getUsername());
                avatar.setUser(user);
                avatar.setAvatar(list.get(0));
                avatar.save(new SaveListener() {

                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null) {
                            toast("设置成功");
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

    public void hideToolbarAndFb(int flag) {
        if (Animator_toolbar != null && Animator_toolbar.isRunning()) {

        }
        if (flag == 0) {
            Animator_toolbar = ObjectAnimator.ofFloat(home_bar, "translationY", home_bar.getTranslationY(), 0);
            fab.show();
        } else {
            Animator_toolbar = ObjectAnimator.ofFloat(home_bar, "translationY", home_bar.getTranslationY(), -home_bar.getHeight());
            fab.hide();
        }
        Animator_toolbar.start();
    }

    public void hideBottomBar(int flag) {
        if (flag == 0) {
            Animator_bottom = ObjectAnimator.ofFloat(ll_bottom, "translationY", ll_bottom.getTranslationY(), 0);
        } else {
            Animator_bottom = ObjectAnimator.ofFloat(ll_bottom, "translationY", ll_bottom.getTranslationY(), ll_bottom.getHeight());
        }
        Animator_bottom.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
    }

    /**
     * onTabClicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_discovery:
                index = 0;
                break;
            case R.id.btn_conversation:
                index = 1;
                break;
            case R.id.btn_address_list:
                index = 2;
                break;
            case R.id.btn_setting:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }

        if (index < 4 && currentTabIndex < 4) {
            mTabs[currentTabIndex].setSelected(false);
            // set current tab as selected.
            mTabs[index].setSelected(true);
        } else if (index < 4 && currentTabIndex > 4) {
            // set current tab as selected.
            mTabs[index].setSelected(true);
        } else if (index > 4 && currentTabIndex < 4) {
            mTabs[currentTabIndex].setSelected(false);
            // set current tab as selected.
        } else if (index > 4 && currentTabIndex > 4) {

        }
        currentTabIndex = index;
    }

    private Map<String, EaseUser> getContacts() {
        final Map<String, EaseUser> contacts = new HashMap<>();
        BmobQuery<User> query = new BmobQuery<User>();
        query.setLimit(100);
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereRelatedTo("friends_relation", new BmobPointer(user));
        query.findObjects(new FindListener<User>() {

            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list == null || list.equals("")) {
                        toast("null");
                        return;
                    }
//                    Log.i("duke", "list大小：" + list.size());
                    for (User user : list) {
//                        Log.i("duke", user.getUsername());
                        final EaseUser easeUser = new EaseUser(user.getUsername());
                        if (user.getAvatarUrl() != null) {
                            easeUser.setAvatar(user.getAvatarUrl());
                        } else {
                            BmobQuery<Avatar> query = new BmobQuery<Avatar>();
                            query.addWhereEqualTo("user", user);
                            query.findObjects(new FindListener<Avatar>() {
                                @Override
                                public void done(List<Avatar> list, BmobException e) {
                                    if (e == null) {
                                        if (list == null || list.equals("")) {
                                            toast("null");
                                            return;
                                        }
                                        if(list.get(0)!=null&&list.get(0).getAvatar()!=null){
                                            easeUser.setAvatar(list.get(0).getAvatar().getUrl());
                                        }
                                    }

                                }
                            });
                        }
                        if (user.getNick() != null) {
                            easeUser.setNick(user.getNick());
                        }
                        EaseCommonUtils.setUserInitialLetter(easeUser);
                        if (MyApplication.appInstance.contacts == null) {
                            MyApplication.appInstance.contacts = new HashMap<String, EaseUser>();
                        }

                        getAppInstance().contacts.put(user.getUsername(), easeUser);
                        contacts.put(user.getUsername(), easeUser);
                    }
                } else {
                    toast("获取联系人失败" + e);
                }
            }

        });
        return contacts;
    }
}
