function createMap() {
    const map = new google.maps.Map(
        document.getElementById('map'),
        {center: {lat: 33.97919947216425, lng: -117.32849493862577}, zoom: 16});

    const trexMarker = new google.maps.Marker({
      position: {lat: 33.97919947216425, lng: -117.32849493862577},
      map: map,
      title: 'Cycle Bun'
    });
} 