package com.google.sps.servlets;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.TimestampValue;
import com.google.gson.Gson;
import com.google.sps.data.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

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
    return new Event(
        id, name, description, location_name, date, event_type, subject, position, school_id);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    if (request.getParameterMap().containsKey("school_id")) {
      Long school_id = Long.parseLong(request.getParameter("school_id"));
      Query<Entity> query =
          Query.newEntityQueryBuilder()
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
    } else {
      // ToDo: Seokha Kang implement this method.
      response.getWriter().println("You've called the get request");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the value entered in the form
    Double event_Lat_D = Double.parseDouble(request.getParameter("lat"));
    Double event_Lng_D = Double.parseDouble(request.getParameter("lng"));
    String event_name = Jsoup.clean(request.getParameter("name"), Safelist.basic());
    String event_desc = Jsoup.clean(request.getParameter("description"), Safelist.basic());
    String event_loc = Jsoup.clean(request.getParameter("location_name"), Safelist.basic());
    TimestampValue event_time =
        TimestampValue.of(Timestamp.parseTimestamp(request.getParameter("utc-date")));
    String event_type = request.getParameter("event_type");
    String event_sub = request.getParameter("subject");
    Long event_school_L = Long.parseLong(request.getParameter("school_id"));

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

    response.sendRedirect(request.getRequestURL().toString().split("event")[0]
                          + "directory-page/directory.html?school_id=" + event_school_L);
  }
}
