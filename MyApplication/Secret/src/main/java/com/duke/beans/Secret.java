package com.duke.beans;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class Secret extends BmobObject implements Serializable{
    private int id;
    private String text;
    private int textColor;
    private int bgColor;
    private String weather;
    private LatLng location;
    private String username;
    private boolean isCollected;
    private int collectedNum;
    private String collectedUsers;
    private User author;//帖子的发布者，这里体现的是一对一的关系，该帖子属于某个用户
    private SecretImage image;//帖子图片
    private BmobRelation likes;//多对多关系：用于存储喜欢该帖子的所有用户
    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public SecretImage getImage() {
        return image;
    }

    public void setImage(SecretImage image) {
        this.image = image;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public int getCollectedNum() {
        return collectedNum;
    }

    public void setCollectedNum(int collectedNum) {
        this.collectedNum = collectedNum;
    }

    public String getCollectedUsers() {
        return collectedUsers;
    }

    public void setCollectedUsers(String collectedUsers) {
        this.collectedUsers = collectedUsers;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
