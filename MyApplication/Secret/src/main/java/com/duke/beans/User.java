package com.duke.beans;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobUser{
    private String sex;
    private String friends;
    private String nickname;
    private BmobRelation friends_relation;//多对多关系：用于存储喜欢该帖子的所有用户
    private String slogan;


    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public BmobRelation getFriends_relation() {
        return friends_relation;
    }

    public void setFriends_relation(BmobRelation friends_relation) {
        this.friends_relation = friends_relation;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
