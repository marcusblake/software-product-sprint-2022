package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.LatLng;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.Map;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import com.google.sps.data.Event;

@WebServlet("/event")
public class EventServlet extends HttpServlet {
    private Event createEventFromEntity(Entity entity) {
        Long id = entity.getKey().getId();
        String name = entity.getString("name");
        String description = entity.getString("description");
        String location_name = entity.getString("location_name");
        String date = entity.getTimestamp("date").toString();
        String event_type = entity.getString("event_type");
        String subject = entity.getString("subject");
        LatLng position = entity.getLatLng("position");
        Long school_id = entity.getLong("school_id");
        return new Event(id, name, description, location_name, date, event_type, subject, position, school_id);
    }

    @Override 
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        
        if (request.getParameterMap().containsKey("school_id")) {
            Long school_id = Long.parseLong(request.getParameter("school_id"));
            Query<Entity> query = Query.newEntityQueryBuilder()
                                       .setKind("Event")
                                       .setFilter(PropertyFilter.eq("school_id", school_id))
                                       .build();
            QueryResults<Entity> results = datastore.run(query);

            List<Event> events = new ArrayList<>();
            while (results.hasNext()) {
                Entity entity = results.next();
                Event event = createEventFromEntity(entity);
                events.add(event);
            }
          
            Gson gson = new Gson();
          
            response.setContentType("application/json;");
            response.getWriter().println(gson.toJson(events));
        } 
        else {
            // ToDo: Seokha Kang implement this method.
            response.getWriter().println("You've called the get request");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
            Collectors.joining("\n"));

        // Error handling: return error if jsonBody is empty
        if (jsonBody == null || jsonBody.trim().length() == 0) {
            return;
        }

        // Parse the jsonBody String to a Gson object
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String,String> myMap = gson.fromJson(jsonBody, type);
        
        // Get the value entered in the form
        String event_name = myMap.get("name");
        String event_Lat = myMap.get("lat");
        String event_Lng = myMap.get("lng");
        String event_n = request.getParameter("name");
        String event_desc = request.getParameter("description");
        String event_loc = request.getParameter("location_name");
        String event_time = request.getParameter("date");
        String event_type = request.getParameter("event_type");
        String event_sub = request.getParameter("subject");

        // Check if the content is read properly

        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        
        // redirect to the event-info page
        //response.sendRedirect("");
    }
}
