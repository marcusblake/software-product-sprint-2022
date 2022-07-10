/** Global variables. */
globals = {
    school_id: 0
}

/** Functions to execute on load. */
window.onload = function() {
    this.getSchoolId();
    this.getHeader();
    this.getEvents();
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

function getEvents() {

}

// /** Fetches tasks from the server and adds them to the DOM. */
// function displayEvents() {
//     fetch(`/event?school_id=${globals.school_id}`).then(response => response.json()).then((events) => {
//         const taskListElement = document.getElementById('task-list');
//         tasks.forEach((task) => {
//             taskListElement.appendChild(createTaskElement(task));
//         })
//     });
// }
  