package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {

    private final long id;

    private String user_name;

    // // plan to implement a country dropdown
    // private String user_location;

    private String content;

    private long timestamp;

    private String formatted_time;

    private double sentiment_score;

    /* Comment:
        id: unique id from Datastore
        user_name: The full name of the user posting the comment.
        user_location: Location (Country) of the comment poster.
        content: the comment.
        timestamp: System time user posted the comment
        score: Score of comment sentiment (value between -1 and 1, based on how positive)
    */
    public Comment(long id, String name, String content, long timestamp, double sentiment_score) {
        this.id = id;
        this.user_name = name;
        this.content = content;
        this.timestamp = timestamp;
        this.sentiment_score = sentiment_score;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");    
        Date date = new Date(timestamp);
        formatted_time = sdf.format(date);
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return user_name;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        return formatted_time;
    }

    public double getSentimentScore() {
        return sentiment_score;
    }

}