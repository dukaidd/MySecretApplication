package com.duke.beans;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobUser {
    private String sex;

    private String friends;
    private String nick;
    private BmobRelation friends_relation;//多对多关系：用于存储喜欢该帖子的所有用户
    private User friends_pointer;
    private BmobPointer avatar_pointer;
    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public BmobPointer getAvatar_pointer() {
        return avatar_pointer;
    }

    public void setAvatar_pointer(BmobPointer avatar_pointer) {
        this.avatar_pointer = avatar_pointer;
    }

    public User getFriends_pointer() {
        return friends_pointer;
    }

    public void setFriends_pointer(User friends_pointer) {
        this.friends_pointer = friends_pointer;
    }

    public BmobRelation getFriends_relation() {
        return friends_relation;
    }

    public void setFriends_relation(BmobRelation friends_relation) {
        this.friends_relation = friends_relation;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


}
