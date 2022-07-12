package com.google.sps.data;

import com.google.cloud.datastore.LatLng;

public class Event {
    private Long id;
    private String name;
    private String description;
    private String location_name;
    private String date;
    private String event_type;
    private String subject;
    private LatLng position;
    private Long school_id;

    private static String[] event_types = {"Study", "Social"};
    private static String[] subjects = {"English", "History", "Science", "Math"};

    public Event(Long id, String name, String description, String location_name, String date,
                 String event_type, String subject, LatLng position, Long school_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location_name = location_name;
        this.date = date;
        this.event_type = event_type;
        this.subject = subject;
        this.position = position;
        this. school_id = school_id;
    }

    public static String[] getEventTypes() {
        return event_types;
    }

    public static String[] getSubjects() {
        return subjects;
    }
}
