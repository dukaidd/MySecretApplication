package com.duke.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by dukaidd on 2016/8/9.
 */

public class Avatar extends BmobObject{
    private BmobFile avatar;
    private User user;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BmobFile getAvatar() {
        return avatar;
    }
    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

}
