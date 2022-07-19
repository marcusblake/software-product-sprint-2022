function loadHomeBtn() {
    fetch('../common.html')
    .then(response => response.text())
    .then(text => document.getElementById('homebtn').innerHTML = text);
}