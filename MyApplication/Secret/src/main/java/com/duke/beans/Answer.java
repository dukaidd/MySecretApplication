package com.duke.beans;

/**
 * Created by dukaidd on 2016/8/4.
 */

public class Answer {
    private String content;
    private long time;
    private String username;
    private Integer NumLiked;

    public Answer(String content, long time, String username) {
        this.content = content;
        this.time = time;
        this.username = username;
        this.NumLiked = 0;
    }

    public Answer(String content, long time, String username, Integer numLiked) {
        this.content = content;
        this.time = time;
        this.username = username;
        NumLiked = numLiked;
    }

    @Override
    public String toString() {
        return "Answer{" +
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
