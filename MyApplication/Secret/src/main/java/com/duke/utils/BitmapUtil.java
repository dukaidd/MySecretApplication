package com.duke.utils;

import com.duke.secret.R;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BitmapUtil {
	public static BitmapUtils getBitUtil(Context context) {
		BitmapUtils bu = new BitmapUtils(context);
		bu.configMemoryCacheEnabled(true);
		bu.configDiskCacheEnabled(true);
		bu.configDefaultLoadFailedImage(R.drawable.avator_profile_default);
		return bu;
	}

}
