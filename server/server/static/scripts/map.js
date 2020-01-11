var incendies = [];

function displayFire(){
    //Si: les feux sont affichés
    if(feu_affiche==true){
        //copie du tableau markerFeuArray
        markerFeuArray_dump = markerFeuArray.slice(0);
        for(i=0;i<markerFeuArray.length;i++){
            map.removeLayer(markerFeuArray[i]);
        }
        feu_affiche=false;
    }
    //Sinon: on affiche les feux
    else{
        for(i=0;i<markerFeuArray.length;i++){
            map.addLayer(markerFeuArray[i]);
        }
        feu_affiche=true;
    }
}

function setIncendies(payload) {
  for (var data of payload) {
    incendies.push({
      id: data.id,
      nom: data.label,
      intensite: data.intensity,
      lat: data.geolocation.lat,
      lon: data.geolocation.lon,
    })
  }

  drawIncendies();
}

function updateIncendie(payload) {
  var incendie = incendies.find(it => it.id == payload.id);

  incendie.lat = payload.geolocation.lat;
  incendie.lon = payload.geolocation.lon;
  incendie.intensite = payload.intensity;

  drawIncendies();
}

function drawIncendies() {
  	//Incendies
    for (var incendie of incendies) {
      if (incendie.intensite == 0) {
        continue;
      }

      if (incendie.intensite <= 4){
          var icon = "feu_petit.gif";
      }else if(incendie.intensite >= 5 && incendie.intensite <= 7){
          var icon = "feu_moyen.gif";
      }else{
          var icon = "feu_grand.gif";
      }

      var iconeIncendie = L.icon({
          iconUrl: "/static/images/"+ icon,
          iconSize: [64, 64],
          iconAnchor: [0, 0],
          popupAnchor: [0, 0],
      });

      var marker = L.marker([incendie.lat, incendie.lon], { icon: iconeIncendie }).addTo(map);
      markerFeuArray.push(marker);
      nb_incendies++;

  // modification de la popup des incendies
  var customPopup = "<b>Incendie n°"+incendie.id+"</b></br>"+
          "<div>Informations : "+incendie.nom+"</div>"+
          "<div>Intensité : "+incendie.intensite+"</div>";

  // map.addLayer(markerClusters);

  // options pour les incendies
  var customOptions =
    {
    'maxWidth': '400',
    'width': '400',
    'className' : 'popupCustom'
    }
      marker.bindPopup(customPopup, customOptions);
      // markerClusters.addLayer(marker);
  } // fin for incendies
}

function initMap(lat, lon){
    //var map = L.map('map').setView([lat, lon], 13);
    //Token pour Mapbox
    L.mapbox.accessToken = 'pk.eyJ1IjoibXJuNzMiLCJhIjoiY2s0OGZ4OXpoMGt3NTNlcGE2Z3RkZGVuZCJ9.XJWyc-rPuhQo-UBmme1vpQ';
    var j = 0;
    markerClusters = L.markerClusterGroup();
    //Camions
    markerCamionArray = {};
    movingMarkerArray = {};
    //Feux
    markerFeuArray = new Array();

    var mapboxTiles = L.tileLayer('https://api.mapbox.com/styles/v1/mapbox/streets-v11/tiles/{z}/{x}/{y}?access_token=' + L.mapbox.accessToken, {
       attribution: '© <a href="https://www.mapbox.com/feedback/">Mapbox</a> © <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    map = L.map('map')
    .addLayer(mapboxTiles)
    .setView([lat, lon], 14);

    // Moving Maker pour le déplacement des camions sur les lignes !! coté simulateur
    /*var myMovingMarker = L.Marker.movingMarker([[lat+0.001, lon+0.01],[lat-0.001, lon-0.001]],
        20000, {autostart: true});*/

    /*
    var greenIcon = L.icon({
        iconUrl: '/static/images/camion-pompier.png',
    });

    myMovingMarker.options.icon = greenIcon;

    map.addLayer(myMovingMarker);*/

    // var camions = {};

	//Carte
    /*L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
		attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
        minZoom: 1,
        maxZoom: 20
    }).addTo(map);*/

    //Camions
    // for (camion in camions) {
    //     var iconeCamion = L.icon({
    //         iconUrl: "/static/images/camion-pompier.png",
    //         iconSize: [64, 64],
    //         iconAnchor: [0, 0],
    //         popupAnchor: [0, 0],
    //     });

    //     var markerCamion = L.marker([camions[camion].lat, camions[camion].lon], { icon: iconeCamion }).addTo(map);
    //     markerCamionArray.push(markerCamion);
    //     map.addLayer(markerCamionArray[j]);
    //     j++;
    // } // fin for camions

    map.addLayer(markerClusters);

	/* calcul côté simulateur
	L.Routing.control({
		waypoints: [
			L.latLng(lat+0.001, lon+0.01),
			L.latLng(lat-0.001, lon-0.001)
		]
	}).addTo(map);*/

}

function checkEtatCamion(){
  var listeCamionsEl = document.getElementById('liste-camions');

    //console.log(movingMarkerArra
    for(var id in movingMarkerArray) {
      var camion = movingMarkerArray[id];
      var ligneEl = document.getElementById("ligne_" + id);

      if (!ligneEl) {
        ligneEl = document.createElement("tr");
        ligneEl.id = "ligne_" + id;
        ligneEl.className = "bg-success";

        ligneEl.innerHTML = '<th scope="row">'+ id +'</th>'+
            '<td>'+
                '<img style="display: inline-block;" src="/static/images/info_camion.jpg" alt="" border=3 height=100 width=100></img>'+
                '<div id="etat_camion_'+id+'" style="display: inline-block;color: green;" style="color: green;">Etat du camion : Disponible</div>'+
            '</td>';

        listeCamionsEl.insertAdjacentElement('beforeend', ligneEl);
      }

        if(camion.isRunning()){
            //etatCamion[idCamion] = "En déplacement";
            //Modification du code HTML
            var new_html = 'Etat du camion : En déplacement';
            document.getElementById('etat_camion_' + id).style.color = "red";
            ligneEl.classList.remove('bg-success');
            ligneEl.classList.add('bg-danger');
        }else{
            var new_html = 'Etat du camion : Disponible';
            document.getElementById('etat_camion_' + id).style.color = "green";
            ligneEl.classList.add('bg-success');
            ligneEl.classList.remove('bg-danger');
            // movingMarkerArray.shift();
        }
        document.getElementById('etat_camion_' + id).innerHTML = new_html;
    }

    //Si: le camion est disponible
    /*if(myMovingMarker.isEnded()){
        document.getElementById('1').innerHTML = 'Disponible';
    }*/
}

function moveCamion(idCamion, lon, lat){
    //etatCamion = new Array();
    /*for(i=0;i<markerCamionArray.length;i++) {
        if(i == idCamion){*/

          var markerCamion = movingMarkerArray[idCamion];

          if (!markerCamion) {
            var iconeCamion = L.icon({
                iconUrl: "/static/images/camion-pompier.png",
                iconSize: [64, 64],
                iconAnchor: [0, 0],
                popupAnchor: [0, 0],
            });

            markerCamion = L.marker([lat, lon], { icon: iconeCamion }).addTo(map);
            movingMarkerArray[idCamion] = markerCamion;
            map.addLayer(markerCamion);
          } else {
            map.removeLayer(markerCamion);

            oldLat = markerCamion._latlng.lat;
            oldLon = markerCamion._latlng.lng;

            var myMovingMarker = L.Marker.movingMarker([[oldLat, oldLon],[lat, lon]],
                1000, {autostart: true});

            var greenIcon = L.icon({
                iconUrl: '/static/images/camion-pompier.png',
            });

            myMovingMarker.options.icon = greenIcon;

            map.addLayer(myMovingMarker);
            movingMarkerArray[idCamion] = myMovingMarker;

            // markerCamion._latlng.lat = lat
            // markerCamion._latlng.lat = lon
          }


        //}
    //} // fin for

    //console.log(lon);

    /*var iconeCamion = L.icon({
        iconUrl: "/static/images/camion-pompier.png",
        iconSize: [64, 64],
        iconAnchor: [0, 0],
        popupAnchor: [0, 0],
    });

    L.marker([lat, lon], { icon: iconeCamion }).addTo(map);*/
}

function initWebSocket() {
    const ws = new WebSocket(`ws://${location.host}/ws/client`);

    ws.onerror = console.error;

    ws.onopen = function onWebSocketOpen() {
        ws.send(JSON.stringify({
            action: 'get_sensors',
            payload: null,
        }));
    };

    ws.onmessage = function onWebSocketMessage(event) {
        const msg = JSON.parse(event.data);
        const { action, payload } = msg;


        switch (action) {
            case 'sensors_set':
                setIncendies(payload);
                break;
            case 'sensor_update':
                updateIncendie(payload);
                break;
            case 'truck_geolocation':
                moveCamion(payload.id, payload.geolocation.lon, payload.geolocation.lat);
                break;
        }
    }
}

// function initWebSocket(exampleSocket, lon, lat, i, index){

//         i=i+0.002;
//         return [idCamion, newLat, newLon] = [index, lat+i, lon+i];
// }

window.onload = function(){
    var [lat, lon] = [45.750000, 4.850000];
    var i=0;
    feu_affiche = true;
    nb_incendies = 0;

    document.getElementById("displayFire").addEventListener("click", displayFire);

    // Fonction d'initialisation qui s'exécute lorsque le DOM est chargé
    initMap(lat, lon);

    initWebSocket();

    // keys_camions = Object.keys(camions);
    // for(index=0;index<keys_camions.length;index++){
    //     i=i+0.005;
    //     var [idCamion, newLat, newLon] = receiveDataFromPython(exampleSocket, lon, lat, i, index);
    //     moveCamion(idCamion, newLon, newLat);
    // }

    //i=i+0.02;

    window.setInterval(function(){
        checkEtatCamion();
    }, 1000);
};

