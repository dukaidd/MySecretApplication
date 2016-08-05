package com.duke.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
	public static String getTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(new Date(time));
	}
}
