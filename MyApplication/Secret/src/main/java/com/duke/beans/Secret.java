package com.duke.beans;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Secret extends BmobObject {
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
    private List<Answer> answers;

    public List<Answer> getAnswers() {

        return answers;
    }

    public void setAnswers(List<Answer> answers) {

        this.answers = answers;
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
