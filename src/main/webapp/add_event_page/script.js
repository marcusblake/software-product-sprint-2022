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
    var event_desc = document.getElementById("description").value;
    var data = { 
        "name": event_name,
        "description": event_desc,
        "lat": currentLatitude,
        "lng": currentLongitude,
    }
    fetch("/event",{
         method: "POST",
         body: JSON.stringify(data),
    });
}
 