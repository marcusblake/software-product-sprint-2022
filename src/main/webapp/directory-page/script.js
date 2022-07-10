/** Global variables. */
globals = {
    school_id: 0,
    events: [],
    max_descrip_len: 150,
    user_coords: new google.maps.LatLng(0, 0)
}

/** Functions to execute on load. */
window.onload = function() {
    this.getSchoolId();
    this.getHeader();
    this.loadEvents();
}

/** Get school ID from URL parameter. */
function getSchoolId() {
    search_params = new URLSearchParams(window.location.search);
    globals.school_id = search_params.get("school_id");
}

/** Update the header with the school name. */
async function getHeader() {
    response = await fetch(`/school?school_id=${globals.school_id}`);
    school_info = await response.json();
    document.getElementById("header").innerHTML = `Happening in ${school_info.name}`;
}

/** Make subject options visible when "study" is checked. */
function changeSubjectsVisibility() {
    if (document.getElementById("study").checked) {
        display = 'inline';
    }
    else {
        display = 'none';
    }
    document.getElementById("subject-header").style.display = display;
    document.getElementById("subject-options").style.display = display;
}

/** Make sure at least one event type checked when submitting the filter. */
function eventTypeChecked() {
    if (!document.getElementById("study").checked && !document.getElementById("social").checked) {
        alert("Please select at least one event type.");
        return false;
    }
    return true;
}

/** Fetch the events from the backend. */
async function loadEvents() {
    globals.events = await fetch(`/event?school_id=${globals.school_id}`).then(response => response.json());
    resortEvents('time');
    displayEvents();
}

/** Sort events by time or distance. */
async function resortEvents(order) {
    if (order == 'time') {
        globals.events.sort(compareTime);
        displayEvents();
    }
    else { // order == 'distance
        try {
            let position = await getUserLocation();
            globals.user_coords = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
            globals.events.sort(compareDistance);
            displayEvents();
        }
        catch(e) {
            alert('Unable to retrieve your location.');
            document.getElementById('sortTime').checked = true;
        }
    }
}

/** Compare function for sorting by time. */
function compareTime(event1, event2) {
    return new Date(event1.date) - new Date(event2.date);
}

/** Compare function for sorting by distance. */
function compareDistance(event1, event2) {
    position1 = new google.maps.LatLng(event1.position.latitude, event1.position.longitude);
    position2 = new google.maps.LatLng(event2.position.latitude, event2.position.longitude);
    distance1 = google.maps.geometry.spherical.computeDistanceBetween(globals.user_coords, position1);
    distance2 = google.maps.geometry.spherical.computeDistanceBetween(globals.user_coords, position2);
    return distance1 - distance2;
}

/** Gets the user location. */
function getUserLocation() {
    // Need to promisify the geolocation request, https://stackoverflow.com/a/62594598
    return new Promise((resolve, reject) =>
        navigator.geolocation.getCurrentPosition(resolve, reject)
    );
}

/** Gets events and displays them on the page. */
function displayEvents() {
    const eventListElement = document.getElementById('event-list');
    eventListElement.innerHTML = '';
    globals.events.forEach((event) => {
        eventListElement.appendChild(createEventElement(event));
    });
}

/** Creates a list item in the events list. */
function createEventElement(event) {
    eventElement = document.createElement('li');
    eventElement.setAttribute('onclick', `location.href = '../event-info-page/eventInfo.html?event_id=${event.id}'`);

    const nameElement = document.createElement('p');
    nameElement.className = 'event-name';
    nameElement.innerHTML = event.name;

    const descripElement = document.createElement('p');
    descripElement.className = 'event-descrip';
    descripElement.innerHTML = event.description.substring(0, globals.max_descrip_len);
    if (event.description.length > globals.max_descrip_len) {
        descripElement.innerHTML += '...';
    }

    const locDateElement = document.createElement('p');
    locDateElement.className = 'event-loc-date';
    date = new Date(event.date);
    formatted_date = date.toLocaleString('en-US', {
        month: "short",
        day: "numeric",
        year: "numeric",
        hour: "numeric", 
        minute: "2-digit"
    })
    locDateElement.innerHTML = `${event.location_name} | ${formatted_date}`;

    eventElement.append(nameElement, descripElement, locDateElement);    
    return eventElement;
}
