package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// custom
import com.google.sps.data.School;

@WebServlet("/school")
public class SchoolServlet extends HttpServlet {
    private School createSchoolFromEntity(Entity entity) {
        Long id = entity.getKey().getId();
        String name = entity.getString("name");
        LatLng position = entity.getLatLng("position");
        return new School(id, name, position);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        if (request.getParameterMap().containsKey("school_id")) {
            Long school_id = Long.parseLong(request.getParameter("school_id"));
            KeyFactory keyFactory = datastore.newKeyFactory().setKind("School");
            Entity entity = datastore.get(keyFactory.newKey(school_id));
            School school = createSchoolFromEntity(entity);

            Gson gson = new Gson();
            response.setContentType("application/json;");
            response.getWriter().println(gson.toJson(school));
        }
        else { /* Get all schools in data */
            Query<Entity> query = Query.newEntityQueryBuilder().setKind("School").setOrderBy(OrderBy.asc("name")).build();
            QueryResults<Entity> results = datastore.run(query);

            List<School> schools = new ArrayList<>();
            while (results.hasNext()) {
                Entity entity = results.next();
                School school = createSchoolFromEntity(entity);
                schools.add(school);
            }

            Gson gson = new Gson();
            response.setContentType("application/json;");
            response.getWriter().println(gson.toJson(schools));
        }
    }
}
