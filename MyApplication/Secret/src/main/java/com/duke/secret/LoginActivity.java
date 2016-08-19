package com.duke.secret;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.duke.base.BaseActivity;
import com.duke.beans.User;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    public static final int REQUEST_CODE = 1;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText mUsernameView, mPasswordView;
    private View mLoginFormView;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        getSharedPreferences("times", Context.MODE_PRIVATE).edit().putBoolean("isFirst", false).apply();
        mUsernameView = (EditText) findViewById(R.id.login_username);
        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_button || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button mSignInButton = (Button) findViewById(R.id.login_button);
        Button mRegisterButton = (Button) findViewById(R.id.login_register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("正在登录...");
        pd.show();
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            pd.dismiss();
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            pd.dismiss();
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.login(new SaveListener<User>() {

                @Override
                public void done(User user, final BmobException e) {
                    if (e == null) {
                        EMChatManager.getInstance().login(username, password, new EMCallBack() {// 回调
                            @Override
                            public void onSuccess() {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        EMGroupManager.getInstance().loadAllGroups();
                                        EMChatManager.getInstance().loadAllConversations();
                                        Log.d("main", "登陆聊天服务器成功！");
                                        toast("登录成功");
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        pd.dismiss();
                                        LoginActivity.this.finish();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }

                            @Override
                            public void onError(int code, final String message) {
                                Log.d("duke", "登陆聊天服务器失败！");
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pd.dismiss();
                                        mPasswordView.setError("登录失败:" + message);
                                        mPasswordView.requestFocus();
                                    }
                                });

                            }
                        });

                    } else {
                        Log.d("duke", "登陆BMOB服务器失败！");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                mPasswordView.setError("登录失败:" + e);
                                mPasswordView.requestFocus();
                            }
                        });
                    }
                }
            });


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RegisterActivity.RESULT_CODE) {
            String username_content = data.getStringExtra("username");
            mUsernameView.setText(username_content);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

