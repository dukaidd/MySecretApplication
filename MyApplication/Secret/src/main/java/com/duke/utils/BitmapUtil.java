package com.duke.utils;

import android.content.Context;

import com.duke.secret.R;
import com.lidroid.xutils.BitmapUtils;

public class BitmapUtil {
	public static BitmapUtils getBitUtil(Context context) {
		BitmapUtils bu = new BitmapUtils(context);
		bu.configMemoryCacheEnabled(true);
		bu.configDiskCacheEnabled(true);
		bu.configDefaultLoadFailedImage(R.drawable.ic_default_male);
		return bu;
	}
	public  static void setAvatar(){





	}

}
