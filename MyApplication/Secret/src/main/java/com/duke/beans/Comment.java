package com.duke.beans;

/**
 * Created by dukaidd on 2016/8/4.
 */

public class Comment {
    private String content;
    private long time;
    private String username;
    private Integer NumLiked;
    private User user;//评论的用户，Pointer类型，一对一关系
    private Secret secret; //所评论的帖子，这里体现的是一对多的关系，一个评论只能属于一个微博

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }

    public Comment(String content, long time, String username) {
        this.content = content;
        this.time = time;
        this.username = username;
        this.NumLiked = 0;
    }

    public Comment(String content, long time, String username, Integer numLiked) {
        this.content = content;
        this.time = time;
        this.username = username;
        NumLiked = numLiked;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "content='" + content + '\'' +
                ", time=" + time +
                ", username='" + username + '\'' +
                ", NumLiked=" + NumLiked +
                '}';
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    public Integer getNumLiked() {
        return NumLiked;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNumLiked(Integer numLiked) {
        NumLiked = numLiked;
    }
}
