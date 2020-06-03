package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    // Name of user leaving the comment
    private String name;

    // Location of user
    private String location;

    // Text content of comment
    private String content;

    public Comment(String name, String location, String content) {
        this.name = name;
        this.location = location;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getContent() {
        return content;
    }

}