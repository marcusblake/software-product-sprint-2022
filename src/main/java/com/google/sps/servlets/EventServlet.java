package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import com.google.cloud.datastore.Key;
import com.google.sps.data.*;

@WebServlet("/event")
public class EventServlet extends HttpServlet {
    
    @Override 
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
        // ToDo: Seokha Kang implement this method.
        response.getWriter().println("You've called the get request");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        // Get the value entered in the form
        String event_name = request.getParameter("name"); //name
        String event_desc = request.getParameter("description");
        String event_loc = request.getParameter("location_name");
        String event_time = request.getParameter("date");
        String event_type = request.getParameter("event_type");
        String event_sub = request.getParameter("subject");
        response.setContentType("text/html;");
        response.getWriter().println("<p>Name: " + event_name + "</p>");
        
        // redirect to the event-info page
        //response.sendRedirect("");
    }
}
