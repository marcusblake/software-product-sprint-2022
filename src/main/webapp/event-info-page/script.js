window.onload = function() {
    this.getEventId();
    this.loadEventInfo();
    this.loadHomeBtn();
}

function createMap(_lat, _lng) {
    const map = new google.maps.Map(document.getElementById("map"),{
        center: {lat: _lat, lng: _lng},
        zoom: 16,
    });

    const marker = new google.maps.Marker({
        position: {lat: _lat, lng: _lng},
        map: map
    });
}

function getEventId() {
    const urlParams = new URL(location.href).searchParams;
    const event_id = urlParams.get('event_id');
    return event_id;
}

async function loadEventInfo() {
    const event_info = await fetch(`/event?event_id=${getEventId()}`, {method: 'GET'}).then(response => response.json())
    document.getElementById("name").innerHTML = event_info.name;
    document.getElementById("description").innerHTML = event_info.description;
    document.getElementById("location").innerHTML = event_info.location_name;

    var date = new Date(event_info.date);
    var formatted_date = date.toLocaleString('en-US', {
        month: "short",
        day: "numeric",
        year: "numeric",
        hour: "numeric",
        minute: "2-digit"
    });
    document.getElementById("date").innerHTML = formatted_date;

    this.document.getElementById('backbtn').setAttribute('onclick', `location.href = '../directory-page/directory.html?school_id=${event_info.school_id}'`);

    var latitude = parseFloat(event_info.position.latitude);
    var longitude = parseFloat(event_info.position.longitude);
    createMap(latitude, longitude);
}
