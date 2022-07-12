let map;
let school_id = 0;
let school_position;
let initial_lat;
let initial_lng;
let currentLatitude;
let currentLongitude;

window.onload = function() {
    console.log('Hiii');
    this.getSchool();
    this.getPos();
}

function getSchool(){
    new_search = new URLSearchParams(window.location.search);
    school_id = new_search.get("school_id");
    console.log(school_id);
}

async function getPos(){
    console.log('Hiii');
    response = await fetch(`/school?school_id=${school_id}`);
    school_info = await response.json();
    console.log(school_info);
    school_position = school_info.position;
    initial_lat = school_position.latitude;
    initial_lng = school_position.longitude;
    currentLatitude = initial_lat;
    currentLongitude = initial_lng;
}

function initMap(){
    map = new google.maps.Map(document.getElementById("map"),{
        center: school_position,
        zoom: 16,
    });

    var marker = new google.maps.Marker({
        position: school_position,
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
    var event_date = new Date(document.getElementById("date").value).toISOString();
    var event_type = document.getElementById("event_type").value;
    var event_sub = document.getElementById("subject").value;
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
 