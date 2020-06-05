package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    private final long id;

    private String user_name;

    // plan to implement a country dropdown
    private String user_location;

    private String content;

    private long timestamp;

    /* Comment:
        user_name: The full name of the user posting the comment.
        user_location: Location (Country) of the comment poster.
        content: the comment.
        timestamp: System time user posted the comment
    */
    public Comment(long id, String name, String location, String content, long timestamp) {
        this.id = id;
        this.user_name = name;
        this.user_location = location;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return user_name;
    }

    public String getUserLocation() {
        return user_location;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

}