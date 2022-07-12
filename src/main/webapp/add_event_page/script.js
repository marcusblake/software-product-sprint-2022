let map;
let school_id = 0;
let school_position;
let initial_lat;
let initial_lng;
let currentLatitude;
let currentLongitude;

window.onload = function() {
    this.getSchool();
    this.initMap();
    this.document.getElementById('back').setAttribute('onclick', `location.href = '../directory-page/directory.html?school_id=${school_id}'`);
}

function getSchool(){
    new_search = new URLSearchParams(window.location.search);
    school_id = new_search.get("school_id");
}

async function getPos(){
    response = await fetch(`/school?school_id=${school_id}`)
    school_info = await response.json();
    school_position = school_info.position;
    initial_lat = parseFloat(school_position.latitude);
    initial_lng = parseFloat(school_position.longitude);
    currentLatitude = initial_lat;
    currentLongitude = initial_lng;
}

async function initMap(){
    await this.getPos();
    map = new google.maps.Map(document.getElementById("map"),{
        center: {lat:initial_lat, lng:initial_lng},
        zoom: 16,
    });

    var marker = new google.maps.Marker({
        position: {lat:initial_lat, lng:initial_lng},
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
 