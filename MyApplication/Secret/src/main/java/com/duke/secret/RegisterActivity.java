package com.duke.secret;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.duke.base.BaseActivity;
import com.duke.beans.User;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends BaseActivity implements View.OnFocusChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private EditText username, password, repassword, email;
    private RadioGroup sex;
    private Button button;
    private String sex_content = "男";
    public static final int RESULT_CODE = 2;
    private Handler hander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            toast("注册成功");
            Intent data = new Intent();
            data.putExtra("username", (String) msg.obj);
            setResult(RESULT_CODE, data);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        getWindow().setStatusBarColor(Color.parseColor("#C43828"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
        button = (Button) findViewById(R.id.register_btn);
        button.setOnClickListener(this);
        username = (EditText) findViewById(R.id.register_username);
        username.setOnFocusChangeListener(this);
        password = (EditText) findViewById(R.id.register_password);
        repassword = (EditText) findViewById(R.id.register_password_confirm);
        repassword.setOnFocusChangeListener(this);
        email = (EditText) findViewById(R.id.register_email);
        sex = (RadioGroup) findViewById(R.id.register_sex);
        sex.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.register_male) {
            sex_content = "男";
        } else {
            sex_content = "女";
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.register_password_confirm:
                if (!hasFocus) {
                    String password_content = password.getText().toString().trim();
                    String repassword_content = repassword.getText().toString().trim();
                    if (!repassword_content.equals(password_content)) {
                        toast("两次输入密码不一致");
                    }
                }
                break;
            case R.id.register_username:
                if (!hasFocus) {
                    String username_content = username.getText().toString().trim();
                    if (!checkUsername(username_content)) {
                        toast("用户名由 3-10位的字母下划线和数字组成");
                    }
                }
                break;
        }
    }

    private void register() {
        final String username_content = username.getText().toString().trim();
        final String password_content = password.getText().toString().trim();
        String email_content = email.getText().toString().trim();
        if (username_content == null || username_content.equals("")) {
            toast("请输入用户名");
            return;
        } else if (!checkUsername(username_content)) {
            toast("用户名由以字母开头的3-10位的小写字母下划线和数字组成");
            return;
        } else if (password_content == null || password_content.equals("")) {
            toast("请输入密码");
            return;
        } else if (email_content == null || email_content.equals("")) {
            toast("请输入用户名");
            return;
        } else if (!checkEmail(email_content)) {
            toast("请输入正确的邮箱");
        }
        User user = new User();
        user.setUsername(username_content);
        user.setPassword(password_content);
        user.setEmail(email_content);
        user.setSex(sex_content);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                // 调用sdk注册方法
                                EMChatManager.getInstance().createAccountOnServer(username_content, password_content);
                                Message msg = Message.obtain();
                                msg.obj = username_content;
                                hander.sendMessage(msg);

                            } catch (final EaseMobException e) {
                                // 注册失败
                                int errorCode = e.getErrorCode();
                                if (errorCode == EMError.NONETWORK_ERROR) {
                                    Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                    Toast.makeText(getApplicationContext(), "用户已存在！", Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.UNAUTHORIZED) {
                                    Toast.makeText(getApplicationContext(), "注册失败，无权限！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }).start();

                } else {
                    toast("注册失败:" + e);
                }
            }
        });

    }

    public static boolean checkEmail(String email) {
        boolean check = email
                .matches("^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$");
        return check;
    }

    public static boolean checkUsername(String username) {
        boolean check = username.matches("^[a-z][a-z0-9_]{2,9}$");
        return check;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_btn) {
            register();
        }
    }
}

