// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.Comment;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns comment data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

    enum SortCriterion {
        NEWEST_FIRST,
        OLDEST_FIRST,
        NEGATIVE_FIRST,
        POSITIVE_FIRST
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int comment_limit = getCommentLimit(request);
        boolean filter_comments = getCommentFilterValue(request);
        SortCriterion sort_direction = getSortDirection(request);
        List<Comment> comments = getComments(comment_limit, filter_comments, sort_direction);

        response.setContentType("application/json");
        String json = new Gson().toJson(comments);
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Entity commentEntity = getCommentEntity(request);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        // Redirect back to HTML page
        response.sendRedirect("/contact.html");
    }

    /** Returns Comment entity to be stored in Datastore */
    private Entity getCommentEntity(HttpServletRequest request) throws IOException {
        // Get input from the form
        String name = getField(request, "comment-name");
        // String location = getField(request, "comment-location");
        String content = getField(request, "comment-content");
        long timestamp = System.currentTimeMillis();

        // Perform sentiment analysis
        Document doc = Document.newBuilder().setContent(content).setType(Document.Type.PLAIN_TEXT).build();
        LanguageServiceClient languageService = LanguageServiceClient.create();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        
        // Should be float but for some reason I get an error casting from double to float
        // (Datastore stores it as a Double)
        double score = (double) sentiment.getScore();

        languageService.close();

        // Store comment in Datastore
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("user_name", name);
        commentEntity.setProperty("timestamp", timestamp);
        // commentEntity.setProperty("user_location", location);
        commentEntity.setProperty("content", content);
        commentEntity.setProperty("sentiment_score", score);

        return commentEntity;
    }

    /** Returns comment limit value from the request, or -1 if user selected 'all' */
    private int getCommentLimit(HttpServletRequest request) {
        String limit_string = request.getParameter("comment-limit");
        int comment_limit = 0;

        try {
            comment_limit = Integer.parseInt(limit_string);
        } catch (NumberFormatException n) {
            System.err.println("Unexpected value '" + limit_string + "' for comment limit.");
        }

        return comment_limit;
    }

    /** Returns comment filter value from the request (true to filter out negative comments, false otherwise) */
    private boolean getCommentFilterValue(HttpServletRequest request) {
        String filter_string = request.getParameter("comment-filter");
        return Boolean.parseBoolean(filter_string);
    }

    /** Returns comment sort direction */
    private SortCriterion getSortDirection(HttpServletRequest request) {
        String sort_direction = request.getParameter("comment-sort");
        try {
            return SortCriterion.valueOf(sort_direction);
        }
        catch (IllegalArgumentException i) {
            System.out.println("Unexpected sort direction '" + sort_direction + "' given. Displaying unsorted comments.");
            return null;
        }
    }

    /** Returns the field value, or null if empty field. */
    private String getField(HttpServletRequest request, String field) {
        // Get input from form
        String fieldString = request.getParameter(field);

        if (fieldString.length() == 0) return null;
        return fieldString;
    }

    /** 
    * Returns list of comments parsed from datastore entities 
    * num_comments: max number of comments to return. if -1, no max limit.
    */
    private List<Comment> getComments(int num_comments, boolean filter_comments, SortCriterion sort_direction) {
        Query query = getSortedCommentsQuery(sort_direction);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        List<Comment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            if (comments.size() == num_comments) break;

            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("user_name");
            // String location = (String) entity.getProperty("user_location");
            String content = (String) entity.getProperty("content");
            long timestamp = (long) entity.getProperty("timestamp");
            System.out.println(entity.getProperty("sentiment_score") + "is type " + entity.getProperty("sentiment_score").getClass());
            double score = (double) entity.getProperty("sentiment_score");

            // Skip if comment is negative and user asked to filter comments
            if (filter_comments && (score < 0)) {
                continue;
            }

            Comment comment = new Comment(id, name, content, timestamp, score);
            comments.add(comment);
        }

        return comments;
    }

    /** Returns query with specified sort_direction */
    private Query getSortedCommentsQuery(SortCriterion sort_direction) {
        Query query;
        switch (sort_direction) {
            case POSITIVE_FIRST:
                query = new Query("Comment").addSort("sentiment_score", SortDirection.DESCENDING);
                break;
            case NEGATIVE_FIRST:
                query = new Query("Comment").addSort("sentiment_score", SortDirection.ASCENDING);
                break;
            case OLDEST_FIRST:
                query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
                break;
            case NEWEST_FIRST:
                query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
                break;
            default:
                query = new Query("Comment");
                break;
        }

        return query;
    }

}
