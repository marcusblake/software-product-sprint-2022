// globals = {
//     event_id: 0,
// }

// window.onload = function() {
//     this.getEventId();
//     //this.getHeader();
//     //this.loadEvents();
//     //this.document.getElementById('add-event').setAttribute('onclick', `location.href = '../add_event_page/add_event.html?school_id=${globals.school_id}'`);
// }

function createMap(_lat, _lng) {
    const map = new google.maps.Map(
        document.getElementById('map'),
        {center: {lat: _lat, lng: _lng}, zoom: 16});

    const trexMarker = new google.maps.Marker({
      position: {lat: _lat, lng: _lng},
      map: map
    });
}

function getEventId() {
    const urlParams = new URL(location.href).searchParams;
    const event_id = urlParams.get('event_id');
    return event_id;
}

async function loadTasks() {
    const event_info = await fetch(`/event?event_id=${getEventId()}`, {method: 'GET'}).then(response => response.json())
    document.getElementById("name").innerHTML = event_info.name;
    document.getElementById("description").innerHTML = event_info.description;
    document.getElementById("location").innerHTML = event_info.location_name;
    document.getElementById("date").innerHTML = event_info.date;
    console.log(event_info.position);
    console.log(parseFloat(event_info.position.latitude));
    console.log(parseFloat(event_info.position.longitude));
    latitude_float = parseFloat(event_info.position.latitude);
    longitude_float = parseFloat(event_info.position.longitude);
    createMap(latitude_float, longitude_float);
    //console.log(evenet_inf)
    }

// function loadTasks() {
//     fetch(`/eventInfo.html?event_id=${getEventId()}`, {method: 'GET'}).then(response => response.json()).then((events) => {
    //   const taskListElement = document.getElementById('task-list');
    //   tasks.forEach((task) => {
    //     taskListElement.appendChild(createTaskElement(task));
    //   })
        //events.forEach(console.log(event));
//         events.forEach((event) => {
            
//             console.log(event);
//           })
//     });
// }