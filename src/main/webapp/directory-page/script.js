/** Functions to execute on load. */
window.onload = function() {
    this.getHeader();
}

async function getHeader() {
    search_params = new URLSearchParams(window.location.search);
    school_id = search_params.get("school_id");
    response = await fetch(`/school?school_id=${school_id}`);
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