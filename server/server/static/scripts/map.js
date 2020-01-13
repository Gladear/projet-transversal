var map = null;

// Incendies
var feu_affiche = true;
var incendies = {};
var markerIncendies = {};

// Camions
var markerCamionArray = {};
var movingMarkerArray = {};

function displayFire() {
  Object.values(markerIncendies).forEach(
    feu_affiche ? map.removeLayer : map.addLayer
  );
  feu_affiche = !feu_affiche;
}

function setIncendies(payload) {
  for (var data of payload) {
    incendies[data.id] = {
      id: data.id,
      nom: data.label,
      intensite: data.intensity,
      lat: data.geolocation.lat,
      lon: data.geolocation.lon
    };
  }

  drawIncendies();
}

function updateIncendie(payload) {
  var incendie = incendies[payload.id];

  incendie.intensite = payload.intensity;

  drawIncendies();
}

function drawIncendies() {
  //Incendies
  for (var id in incendies) {
    const incendie = incendies[id];
    let marker = markerIncendies[id];

    if (incendie.intensite == 0) {
      if (marker) {
        map.removeLayer(marker);
      }

      continue;
    }

    if (incendie.intensite <= 4) {
      var icon = "feu_petit.gif";
    } else if (incendie.intensite >= 5 && incendie.intensite <= 7) {
      var icon = "feu_moyen.gif";
    } else {
      var icon = "feu_grand.gif";
    }

    var iconeIncendie = L.icon({
      iconUrl: "/static/images/" + icon,
      iconSize: [64, 64],
      iconAnchor: [0, 0],
      popupAnchor: [0, 0]
    });

    if (!marker) {
      marker = L.marker([incendie.lat, incendie.lon], { icon: iconeIncendie });
      marker.addTo(map);

      markerIncendies[id] = marker;
    }

    // modification de la popup des incendies
    var customPopup = `
      <b>Incendie n°${incendie.id}</b><br>
      <div>Informations : ${incendie.nom}</div>
      <div>Intensité : ${incendie.intensite}</div>
    `;

    // options pour les incendies
    var customOptions = {
      maxWidth: "400",
      width: "400",
      className: "popupCustom"
    };

    marker.bindPopup(customPopup, customOptions);
  } // fin for incendies
}

function addCamionMarkerToMap(idCamion, lon, lat) {
  var iconeCamion = L.icon({
    iconUrl: "/static/images/camion-pompier.png",
    iconSize: [64, 64],
    iconAnchor: [0, 0],
  });

  markerCamion = L.marker([lat, lon], { icon: iconeCamion }).addTo(map);
  movingMarkerArray[idCamion] = markerCamion;
  map.addLayer(markerCamion);
}

function addCamionLigneToList(id) {
  var listeCamionsEl = document.getElementById("liste-camions");

  ligneEl = document.createElement("tr");
  ligneEl.id = "ligne_" + id;
  ligneEl.className = "bg-success";

  ligneEl.innerHTML = `
    <th scope="row">${id}</th>
    <td>
      <img style="display: inline-block;" src="/static/images/info_camion.jpg" alt="" border="3" height="100" width="100" />
      <div id="etat_camion_${id}" style="display: inline-block;color: green;" style="color: green;">
        Etat du camion : Disponible
      </div>
    </td>
  `;

  listeCamionsEl.insertAdjacentElement("beforeend", ligneEl);

  return ligneEl;
}

function setCamions(payload) {
  for (var data of payload) {
    addCamionMarkerToMap(data.id, data.geolocation.lon, data.geolocation.lat);
    addCamionLigneToList(data.id);
  }
}

function updateEtatCamion() {
  for (var id in movingMarkerArray) {
    var camion = movingMarkerArray[id];
    var ligneEl = document.getElementById("ligne_" + id);

    if (!ligneEl) {
      ligneEl = addCamionLigneToList(id);
    }

    var etatCamionEl = document.getElementById("etat_camion_" + id);
    if (camion.isRunning && camion.isRunning()) {
      //Modification du code HTML
      var new_html = "Etat du camion : En déplacement";
      etatCamionEl.style.color = "red";
      ligneEl.classList.remove("bg-success");
      ligneEl.classList.add("bg-danger");
    } else {
      var new_html = "Etat du camion : Disponible";
      etatCamionEl.style.color = "green";
      ligneEl.classList.remove("bg-danger");
      ligneEl.classList.add("bg-success");
    }

    etatCamionEl.innerHTML = new_html;
  }
}

function moveCamion(idCamion, lon, lat) {
  var markerCamion = movingMarkerArray[idCamion];

  if (!markerCamion) {
    addCamionMarkerToMap(idCamion, lon, lat);
  } else {
    map.removeLayer(markerCamion);

    oldLat = markerCamion._latlng.lat;
    oldLon = markerCamion._latlng.lng;

    var myMovingMarker = L.Marker.movingMarker(
      [
        [oldLat, oldLon],
        [lat, lon]
      ],
      1000,
      { autostart: true }
    );

    var greenIcon = L.icon({
      iconUrl: "/static/images/camion-pompier.png",
      iconSize: [64, 64],
      iconAnchor: [0, 0],
    });

    myMovingMarker.options.icon = greenIcon;

    map.addLayer(myMovingMarker);
    movingMarkerArray[idCamion] = myMovingMarker;
  }
}

function initMap(lat, lon) {
  //Token pour Mapbox
  L.mapbox.accessToken =
    "pk.eyJ1IjoibXJuNzMiLCJhIjoiY2s0OGZ4OXpoMGt3NTNlcGE2Z3RkZGVuZCJ9.XJWyc-rPuhQo-UBmme1vpQ";

  var mapboxTiles = L.tileLayer(
    "https://api.mapbox.com/styles/v1/mapbox/streets-v11/tiles/{z}/{x}/{y}?access_token=" +
    L.mapbox.accessToken,
    {
      attribution:
        '© <a href="https://www.mapbox.com/feedback/">Mapbox</a> © <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }
  );

  map = L.map("map")
    .addLayer(mapboxTiles)
    .setView([lat, lon], 14);
}

function initWebSocket() {
  const ws = new WebSocket(`ws://${location.host}/ws/client`);

  ws.onerror = console.error;

  ws.onopen = function onWebSocketOpen() {
    ws.send(
      JSON.stringify({
        action: "init",
        payload: null
      })
    );
  };

  ws.onmessage = function onWebSocketMessage(event) {
    const msg = JSON.parse(event.data);
    const { action, payload } = msg;

    switch (action) {
      case "init":
        setCamions(payload.trucks);
        setIncendies(payload.sensors);
        break;
      case "sensor_update":
        updateIncendie(payload);
        break;
      case "truck_geolocation":
        moveCamion(
          payload.id,
          payload.geolocation.lon,
          payload.geolocation.lat
        );
        break;
    }
  };
}

window.onload = function () {
  document.getElementById("displayFire").addEventListener("click", displayFire);

  // Fonction d'initialisation qui s'exécute lorsque le DOM est chargé
  initMap(45.75, 4.85);

  initWebSocket();

  window.setInterval(function () {
    updateEtatCamion();
  }, 1000);
};
