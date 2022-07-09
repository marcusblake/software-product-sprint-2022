package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.gson.Gson;

import com.google.sps.data.School;

@WebServlet("/school")
public class SchoolServlet extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        if (request.getParameterMap().containsKey("school_id")) {
            Long school_id = Long.parseLong(request.getParameter("school_id"));
            KeyFactory keyFactory = datastore.newKeyFactory().setKind("School");
            Entity entity = datastore.get(keyFactory.newKey(school_id));
            
            Long id = entity.getKey().getId();
            String name = entity.getString("name");
            LatLng position = entity.getLatLng("position");
            School school = new School(id, name, position);
            
            Gson gson = new Gson();
            response.setContentType("application/json;");
            response.getWriter().println(gson.toJson(school));
        }
    }
}
