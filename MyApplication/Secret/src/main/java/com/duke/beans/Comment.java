package com.duke.beans;

import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by dukaidd on 2016/8/4.
 */

public class Comment extends BmobObject{
    private String content;
    private String username;
    private Integer NumLiked;
    private User author;//评论的用户，Pointer类型，一对一关系
    private Secret secret; //所评论的帖子，这里体现的是一对多的关系，一个评论只能属于一个微博

    public Comment(String content, String username) {
        this.content = content;
        this.username = username;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }

    public String getContent() {
        return content;
    }


    public String getUsername() {
        return username;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "content='" + content + '\'' +
                ", username='" + username + '\'' +
                ", author=" + author +
                ", secret=" + secret +
                ", NumLiked=" + NumLiked +
                '}';
    }
}
