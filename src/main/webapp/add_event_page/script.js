let map;
let currentLatitude = 32.8811;
let currentLongitude = -117.2376;

function initMap(){
    map = new google.maps.Map(document.getElementById("map"),{
        center: {lat:32.8811, lng:-117.2376},
        zoom: 16,
    });

    var marker = new google.maps.Marker({
        position: {lat:32.8811, lng:-117.2376},
        map: map,
        draggable: true,
    });

    google.maps.event.addListener(marker, 'dragend', function(marker){
        var latLng = marker.latLng; 
        currentLatitude = latLng.lat();
        currentLongitude = latLng.lng();
    }); 
}
window.initMap = initMap;

function submitEvent(){
    var event_name = document.getElementById("name").value;
    var event_des = document.getElementById("description").value;
    var event_loc = document.getElementById("location_name").value;
    var event_date = document.getElementById("date").value;
    var event_type = document.getElementById("event_type").value;
    var event_sub = document.getElementById("subject").value;
    new_search = new URLSearchParams(window.location.search);
    var school_id = new_search.get("school_id");

    var data = { 
        "name": event_name,
        "description": event_des,
        "loc": event_loc,
        "date": event_date,
        "type": event_type,
        "subject": event_sub,
        "lat": currentLatitude,
        "lng": currentLongitude,
        "school_id": school_id,
    }
    
    fetch("/event",{
         method: "POST",
         body: JSON.stringify(data),
    });
}
 