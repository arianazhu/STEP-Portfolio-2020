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
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String limit_string = request.getParameter("comment-limit");
        System.out.println("entering doGet. limit_string is " + limit_string);
        int comment_limit = 0;
        try {
            System.out.println("entering try.");
            comment_limit = Integer.parseInt(limit_string);
        } catch (NumberFormatException n) {
            System.out.println("entering catch.");
            if (limit_string.equals("all")) {
                comment_limit = -1;
            }
            else {
                // Printing error messages?
                System.out.println("Unexpected value '" + comment_limit + "' for comment limit.");
            }
        }
        System.out.println("finished try catch.");
        List<Comment> comments = getComments(comment_limit);

        response.setContentType("application/json");
        String json = new Gson().toJson(comments);
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get input from the form
        String name = getField(request, "comment-name");
        String location = getField(request, "comment-location");
        String content = getField(request, "comment-content");
        long timestamp = System.currentTimeMillis();

        // TODO check which field this pops up
        if (name == null || location == null || content == null) {
            response.setContentType("text/html");
            response.getWriter().println("Please fill in all fields.");
            return;
        }

        // Store comment in Datastore
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("user_name", name);
        commentEntity.setProperty("timestamp", timestamp);
        commentEntity.setProperty("user_location", location);
        commentEntity.setProperty("content", content);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        // Redirect back to HTML page
        response.sendRedirect("/contact.html");
    }

    /** Returns the field value, or null if empty field. */
    private String getField(HttpServletRequest request, String param) {
        // Get input from form
        String fieldString = request.getParameter(param);

        if (fieldString.length() == 0) return null;
        return fieldString;
    }

    /** 
    * Returns list of comments parsed from datastore entities 
    * num_comments: max number of comments to return. if -1, no max limit.
    */
    private List<Comment> getComments(int num_comments) {
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        List<Comment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            if (comments.size() == num_comments) break;

            String name = (String) entity.getProperty("user_name");
            String location = (String) entity.getProperty("user_location");
            String content = (String) entity.getProperty("content");
            long timestamp = (long) entity.getProperty("timestamp");

            Comment comment = new Comment(name, location, content, timestamp);
            comments.add(comment);
        }

        return comments;
    }


}
