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
    this.document.getElementById('date').setAttribute('onclick', this.setMin());
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

    const input = this.document.getElementById("place-input");
    const searchBox = new google.maps.places.SearchBox(input);

    google.maps.event.addListener(searchBox,'places_changed',function(){
        var places = searchBox.getPlaces();
        var bounds = new google.maps.LatLngBounds();
        var i,place;
        for(i=0; place=places[i]; i++){
          bounds.extend(place.geometry.location);
          marker.setPosition(place.geometry.location);
        }
        map.fitBounds(bounds);
        map.setZoom(15);
        currentLatitude = marker.getPosition().lat();
        currentLongitude = marker.getPosition().lng();
    });

    google.maps.event.addListener(marker, 'dragend', function(marker){
        var latLng = marker.latLng; 
        currentLatitude = latLng.lat();
        currentLongitude = latLng.lng();
    }); 
}
window.initMap = initMap;

function checkType(){
    if (this.document.getElementById("event_type").value == "Study") {
        this.document.getElementById("subject1").style.display = '';
        this.document.getElementById("subject2").style.display = '';
    } else {
        this.document.getElementById("subject1").style.display = 'none';
        this.document.getElementById("subject2").style.display = 'none';
    }
}

function setMin(){
    now = new Date();
    formatted_date = now.getFullYear() + '-' +
        String(now.getMonth() + 1).padStart(2, '0') + '-' +
        String(now.getDate()).padStart(2, '0') + 'T' +
        String(now.getHours()).padStart(2, '0') + ':' +
        String(now.getMinutes()).padStart(2, '0');
    this.document.getElementById('date').setAttribute('min', formatted_date);
}

function setHiddenInputs() {
    document.getElementById('school_id').value = school_id;
    document.getElementById('lat').value = currentLatitude;
    document.getElementById('lng').value = currentLongitude;
    document.getElementById('utc-date').value = new Date(document.getElementById('date').value).toISOString();
}
