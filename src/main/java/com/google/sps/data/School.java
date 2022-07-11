package com.google.sps.data;

import com.google.cloud.datastore.LatLng;

public class School {
    private Long id;
    private String name;
    private LatLng position;

    public School(Long id, String name, LatLng position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }
}
