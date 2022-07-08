package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/event")
public class EventServlet extends HttpServlet {
    
    @Override 
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
        // ToDo: Seokha Kang implement this method.
        response.getWriter().println("You've called the get request");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ToDo: Mingyi implment this method.
        response.getWriter().println("You've called the post request");
    }
}
