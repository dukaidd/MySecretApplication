package com.duke.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.duke.app.MyApplication;


public class BaseActivity extends AppCompatActivity {
    protected InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void dialog(String title, String message, int icon, String btn1, String btn2, DialogInterface.OnClickListener l,
                       DialogInterface.OnClickListener l1) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(icon);
        dialog.setPositiveButton(btn1, l);
        dialog.setNegativeButton(btn2, l1);
        dialog.create().show();
    }

    public void toast(String text) {

        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected void hideSoftKeyboard() {
        if (this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (this.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
