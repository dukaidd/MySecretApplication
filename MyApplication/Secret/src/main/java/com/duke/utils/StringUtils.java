package com.duke.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
	public static String getTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(new Date(time));
	}
	public static String parseTime(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("M月d日");
		return sdf2.format(date);
	}
	public  static  String getMonthAndDay(Long time){
		SimpleDateFormat sdf2 = new SimpleDateFormat("M月d日");
		return  sdf2.format(new Date(time));
	}

}
