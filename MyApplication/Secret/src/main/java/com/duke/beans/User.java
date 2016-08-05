package com.duke.beans;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobUser {
	private String sex;
	private BmobFile avatar;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public BmobFile getAvatar() {
		return avatar;
	}

	public void setAvatar(BmobFile avatar) {
		this.avatar = avatar;
	}

}
