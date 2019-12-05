
function initMap(lat, lon){
    var map = L.map('map').setView([lat, lon], 13);
    var i = 1;
    markerClusters = L.markerClusterGroup();

    // Liste de marqueurs (Test sans BDD)
    var incendies = {
        "Point1": { "id": 1, "nom": "Olymp Pressing", "intensite": 1, "lat": lat+0.02, "lon": lon+0.01 },
        "Point2": { "id": 2, "nom": "Pharmacie", "intensite": 5,"lat": lat-0.02, "lon": lon+0.01 },
        "Point3": { "id": 3, "nom": "Collège", "intensite": 8,"lat": lat-0.02, "lon": lon-0.01 },
        "Point4": { "id": 4, "nom": "Casino Shop", "intensite": 1,"lat": lat+0.019, "lon": lon-0.02 }
    };
	
	var camions = {
        "Camion1": { "id": 1, "lat": lat+0.018, "lon": lon+0.01 },	
	};

	//Carte
    L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
        minZoom: 1,
        maxZoom: 20
    }).addTo(map);

	//Incendies
    for (incendie in incendies) {
        if(incendies[incendie].intensite >= 1 && incendies[incendie].intensite <= 4){
            var icon = "feu_petit.svg";
        }else if(incendies[incendie].intensite >= 5 && incendies[incendie].intensite <= 7){
            var icon = "feu_moyen.svg";
        }else{
            var icon = "feu_grand.svg";
        }

        var iconeIncendie = L.icon({
            iconUrl: "public/images/" + icon,
            iconSize: [64, 64],
            iconAnchor: [0, 0],
            popupAnchor: [0, 0],
        });
		
        var marker = L.marker([incendies[incendie].lat, incendies[incendie].lon], { icon: iconeIncendie }).addTo(map);
		
		// modification de la popup des incendies
		var customPopup = "<b>Incendie n°"+incendies[incendie].id+"</b></br>"+
						"<div>Informations : "+incendies[incendie].nom+"</div>"+
						"<div>Intensité : "+incendies[incendie].intensite+"</div>";

		// options pour les incendies
		var customOptions =
			{
			'maxWidth': '400',
			'width': '400',
			'className' : 'popupCustom'
			}
        marker.bindPopup(customPopup, customOptions);
        markerClusters.addLayer(marker);
        i++;
    } // fin for incendies
    
	//Camions
    for (camion in camions) {
        var iconeCamion = L.icon({
            iconUrl: "public/images/camion-pompier.png",
            iconSize: [64, 64],
            iconAnchor: [0, 0],
            popupAnchor: [0, 0],
        });
		
        var marker = L.marker([camions[camion].lat, camions[camion].lon], { icon: iconeCamion }).addTo(map);
        i++;
    } // fin for camions
    
    map.addLayer(markerClusters);
}


window.onload = function(){
    var lat = 45.750000;
    var lon = 4.850000;

    // Fonction d'initialisation qui s'exécute lorsque le DOM est chargé
    initMap(lat, lon);
};

