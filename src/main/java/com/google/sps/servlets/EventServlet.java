package com.google.sps.servlets;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import com.google.sps.data.Event;

@WebServlet("/event")
public class EventServlet extends HttpServlet {
  private static String[] eventFields =
      new String[] {
        "name", "description", "loc", "date", "type", "subject", "lat", "lng", "school_id"
      };

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
    return new Event(
        id, name, description, location_name, date, event_type, subject, position, school_id);
  }

  private Query<Entity> getQueryFromFilterParameters(HttpServletRequest request) {
    Long school_id = Long.parseLong(request.getParameter("school_id"));
    Filter filter = PropertyFilter.eq("school_id", school_id);

    if (request.getParameterMap().containsKey("event_type")) {
      filter =
          CompositeFilter.and(
              filter, PropertyFilter.eq("event_type", request.getParameter("event_type")));
    }

    if (request.getParameterMap().containsKey("subject")) {
      List<Value<String>> values =
          Arrays.asList(request.getParameterValues("subject")).stream()
              .map(StringValue::of)
              .collect(Collectors.toList());
      filter = CompositeFilter.and(filter, PropertyFilter.in("subject", ListValue.of(values)));
    }

    return Query.newEntityQueryBuilder().setKind("Event").setFilter(filter).build();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    if (request.getParameterMap().containsKey("school_id")) {
      Query<Entity> query = getQueryFromFilterParameters(request);
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
    } else {
      Long event_id = Long.parseLong(request.getParameter("event_id"));
      KeyFactory keyFactory = datastore.newKeyFactory().setKind("Event");
      Entity entity = datastore.get(keyFactory.newKey(event_id));
      Event event = createEventFromEntity(entity);

      Gson gson = new Gson();
      response.setContentType("application/json;");
      response.getWriter().println(gson.toJson(event));
      System.out.println(gson.toJson(event));
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String jsonBody =
        new BufferedReader(new InputStreamReader(request.getInputStream()))
            .lines()
            .collect(Collectors.joining("\n"));

    // Error handling: return error if jsonBody is empty
    if (jsonBody == null || jsonBody.trim().length() == 0) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "empty JSON body");
      return;
    }

    // Parse the jsonBody String to a Gson object
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {}.getType();
    Map<String, String> eventJsonAsMap = gson.fromJson(jsonBody, type);

    // Check if all the fields are present
    try {
      checkAllEventFieldsPresent(eventJsonAsMap);
    } catch (ServletException exception) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
      return;
    }

    // Get the value entered in the form
    String event_lat = Jsoup.clean(eventJsonAsMap.get("lat"), Safelist.basic());
    Double event_Lat_D = Double.parseDouble(event_lat);
    String event_lng = Jsoup.clean(eventJsonAsMap.get("lng"), Safelist.basic());
    Double event_Lng_D = Double.parseDouble(event_lng);
    String event_name = Jsoup.clean(eventJsonAsMap.get("name"), Safelist.basic());
    String event_desc = Jsoup.clean(eventJsonAsMap.get("description"), Safelist.basic());
    String event_loc = Jsoup.clean(eventJsonAsMap.get("loc"), Safelist.basic());
    TimestampValue event_time =
        TimestampValue.of(Timestamp.parseTimestamp((eventJsonAsMap.get("date"))));
    String event_type = eventJsonAsMap.get("type");
    String event_sub = eventJsonAsMap.get("subject");
    Long event_school_L = Long.parseLong(eventJsonAsMap.get("school_id"));

    // Store to the Datastore
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Event");
    LatLng position = LatLng.of(event_Lat_D, event_Lng_D);
    FullEntity<IncompleteKey> eventEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("name", event_name)
            .set("description", event_desc)
            .set("location_name", event_loc)
            .set("date", event_time)
            .set("event_type", event_type)
            .set("subject", event_sub)
            .set("position", position)
            .set("school_id", event_school_L)
            .build();
    datastore.put(eventEntity);
  }

  private void checkAllEventFieldsPresent(Map<String, String> eventJson) throws ServletException {
    for (String field : eventFields) {
      if (!eventJson.containsKey(field)) {
        throw new ServletException(
            String.format("The field \"%s\" is missing from the JSON body", field));
      }
    }
  }
}
