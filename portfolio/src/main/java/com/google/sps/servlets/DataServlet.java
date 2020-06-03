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
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private ArrayList<Comment> comments = new ArrayList<>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String json = new Gson().toJson(comments);
        response.getWriter().println(json);
        System.out.println("doGet:");
        System.out.println(json);
    }

    @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get input from the form
        String name = getNameField(request);
        String location = getLocationField(request);
        String content = getContentField(request);
        // TODO check which field this pops up
        if (name == null || location == null || content == null) {
            response.setContentType("text/html");
            response.getWriter().println("Please fill in all fields.");
            return;
        }

        comments.add(new Comment(name, location, content));

        // Redirect back to HTML page
        response.sendRedirect("/contact.html");
  }

  /** Returns the name field, or null if empty field. */
  private String getNameField(HttpServletRequest request) {
      // Get input from form
      String fieldString = request.getParameter("comment-name");

      if (fieldString.length() == 0) return null;
      return fieldString;
  }

  /** Returns the location field, or null if empty field. */
  private String getLocationField(HttpServletRequest request) {
      // Get input from form
      String fieldString = request.getParameter("comment-location");

      if (fieldString.length() == 0) return null;
      return fieldString;
  }

  /** Returns the content field, or null if empty field. */
  private String getContentField(HttpServletRequest request) {
      // Get input from form
      String fieldString = request.getParameter("comment-content");

      if (fieldString.length() == 0) return null;
      return fieldString;
  }
}
