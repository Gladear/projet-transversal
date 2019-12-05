// Test sans BD
/*
var tab_incendies = [];

var incendie1 = new Object();
incendie1.intensite = 1;
tab_incendies.push(incendie1);

var incendie2 = new Object();
incendie2.intensite = 2;
tab_incendies.push(incendie2);

var incendie3 = new Object();
incendie3.intensite = 3;
tab_incendies.push(incendie3);
*/

function initMap(lat, lon){
    var map = L.map('map').setView([lat, lon], 13);
    var i = 1;
    markerClusters = L.markerClusterGroup();

    // Liste de marqueurs
    var points = {
        "Point1": { "intensite": 1, "lat": lat+0.02, "lon": lon+0.01 },
        "Point2": { "intensite": 2,"lat": lat-0.02, "lon": lon+0.01 },
        "Point3": { "intensite": 3,"lat": lat-0.02, "lon": lon-0.01 },
        "Point4": { "intensite": 1,"lat": lat+0.02, "lon": lon-0.01 }
    };

    L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
        minZoom: 1,
        maxZoom: 20
    }).addTo(map);

    for (point in points) {
        console.log(points[point].intensite);
        if(points[point].intensite == 1){
            var icon = "feu_petit.svg";
        }else if(points[point].intensite == 2){
            var icon = "feu_moyen.svg";
        }else{
            var icon = "feu_grand.svg";
        }

        var myIcon = L.icon({
            iconUrl: "public/images/" + icon,
            iconSize: [64, 64],
            iconAnchor: [0, 0],
            popupAnchor: [0, 0],
        });
        var marker = L.marker([points[point].lat, points[point].lon], { icon: myIcon }).addTo(map);
        marker.bindPopup("Incendie n°"+i);
        markerClusters.addLayer(marker);
        i++;
    }
    map.addLayer(markerClusters);
}


window.onload = function(){
    var lat = 45.750000;
    var lon = 4.850000;

    // Fonction d'initialisation qui s'exécute lorsque le DOM est chargé
    initMap(lat, lon);
};

