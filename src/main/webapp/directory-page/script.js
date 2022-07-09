/** Global variables. */
globals = {
    school_id: 0
}

/** Functions to execute on load. */
window.onload = function() {
    this.getSchoolId();
    this.getHeader();
}

function getSchoolId() {
    search_params = new URLSearchParams(window.location.search);
    globals.school_id = search_params.get("school_id");
}

async function getHeader() {
    response = await fetch(`/school?school_id=${globals.school_id}`);
    school_info = await response.json();
    document.getElementById("header").innerHTML = `Happening in ${school_info.name}`;
}

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

function eventTypeChecked() {
    if (!document.getElementById("study").checked && !document.getElementById("social").checked) {
        alert("Please select at least one event type.");
        return false;
    }
    return true;
}
