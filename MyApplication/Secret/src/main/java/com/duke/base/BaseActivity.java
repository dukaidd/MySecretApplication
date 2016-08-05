package com.duke.base;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class BaseActivity extends AppCompatActivity {

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
}
