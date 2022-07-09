let map;
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
        jQ("#latitude").val(currentLatitude);
        jQ("#longitude").val(currentLongitude);
    }); 
}
window.initMap = initMap;