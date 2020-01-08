function displayFire(){
    //Si: les feux sont affichés
    if(feu_affiche==true){
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

function displayTruck(){
    if(camion_affiche==true){
        map.removeLayer(markerCamionArray[0]);
        for(i=0;i<movingMarkerArray_dump.length;i++){
            map.removeLayer(movingMarkerArray_dump[i]);
        }
        camion_affiche=false;
    }else{
        //console.log(movingMarkerArray_dump);
        for(i=0;i<movingMarkerArray_dump.length;i++){
            map.addLayer(movingMarkerArray_dump[i]);
        }
        camion_affiche=true;
    }
}

function initMap(lat, lon){
    //var map = L.map('map').setView([lat, lon], 13);
    //Token pour Mapbox
    L.mapbox.accessToken = 'pk.eyJ1IjoibXJuNzMiLCJhIjoiY2s0OGZ4OXpoMGt3NTNlcGE2Z3RkZGVuZCJ9.XJWyc-rPuhQo-UBmme1vpQ';
    var i = 1;
    var j = 0;
    markerClusters = L.markerClusterGroup();
    //Camions
    markerCamionArray = new Array();
    movingMarkerArray = new Array();
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
        iconUrl: 'public/images/camion-pompier.png',
    });

    myMovingMarker.options.icon = greenIcon;

    map.addLayer(myMovingMarker);*/

    // Liste de marqueurs (Test sans BDD)
    incendies = {
        "Point1": { "id": 1, "nom": "Olymp Pressing", "intensite": 1, "lat": lat+0.02, "lon": lon+0.01 },
        "Point2": { "id": 2, "nom": "Pharmacie", "intensite": 5,"lat": lat-0.02, "lon": lon+0.01 },
        "Point3": { "id": 3, "nom": "Collège", "intensite": 8,"lat": lat-0.02, "lon": lon-0.01 },
        "Point4": { "id": 4, "nom": "Casino Shop", "intensite": 1,"lat": lat+0.019, "lon": lon-0.02 }
    };

    camions = {
        "Camion1": { "id": 0, "lat": lat, "lon": lon },	
        "Camion2": { "id": 1, "lat": lat-0.02, "lon": lon+0.03 },	
        "Camion3": { "id": 2, "lat": lat-0.01, "lon": lon+0.03 },	
        "Camion4": { "id": 3, "lat": lat+0.01, "lon": lon+0.03 },	
        "Camion5": { "id": 4, "lat": lat+0.02, "lon": lon-0.03 },	
    };

	//Carte
    /*L.tileLayer('https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png', {
		attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
        minZoom: 1,
        maxZoom: 20
    }).addTo(map);*/

	//Incendies
    for (incendie in incendies) {
        if(incendies[incendie].intensite >= 1 && incendies[incendie].intensite <= 4){
            var icon = "feu_petit.gif";
        }else if(incendies[incendie].intensite >= 5 && incendies[incendie].intensite <= 7){
            var icon = "feu_moyen.gif";
        }else{
            var icon = "feu_grand.gif";
        }

        var iconeIncendie = L.icon({
            iconUrl: "public/images/"+ icon,
            iconSize: [64, 64],
            iconAnchor: [0, 0],
            popupAnchor: [0, 0],
        });
		
        var marker = L.marker([incendies[incendie].lat, incendies[incendie].lon], { icon: iconeIncendie }).addTo(map);
        markerFeuArray.push(marker);
		
		// modification de la popup des incendies
		var customPopup = "<b>Incendie n°"+incendies[incendie].id+"</b></br>"+
						"<div>Informations : "+incendies[incendie].nom+"</div>"+
						"<div>Intensité : "+incendies[incendie].intensite+"</div>";
						
		map.addLayer(markerClusters);

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
		
        var markerCamion = L.marker([camions[camion].lat, camions[camion].lon], { icon: iconeCamion }).addTo(map);
        markerCamionArray.push(markerCamion);
        map.addLayer(markerCamionArray[j]);
        j++;
    } // fin for camions
    
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
    //copie du tableau movingMarkerArray
    if(movingMarkerArray.length !=0){
        movingMarkerArray_dump = movingMarkerArray.slice(0);
    }
    //console.log(movingMarkerArray);
    var numero=1;
    for(i=0;i<movingMarkerArray_dump.length;i++){
        if(movingMarkerArray_dump[i].isRunning()){
            //etatCamion[idCamion] = "En déplacement";
            //Modification du code HTML 
            var new_html = 'Etat du camion : <strong> En déplacement </strong>';  
            document.getElementById(numero).style.color = "red"; 
            var element = document.getElementById("ligne_"+numero);
            element.classList.remove('bg-success');
            element.classList.add('bg-danger');
        }else{
            var new_html = 'Etat du camion : <strong> Disponible </strong>';
            document.getElementById(numero).style.color = "green";
            var element = document.getElementById("ligne_"+numero);
            element.classList.add('bg-success');
            element.classList.remove('bg-danger');
            movingMarkerArray.shift();
        }
        document.getElementById(numero).innerHTML = new_html;
        numero++;
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
            map.removeLayer(markerCamionArray[idCamion]);
            oldLat = markerCamionArray[idCamion]._latlng.lat;
            oldLon = markerCamionArray[idCamion]._latlng.lng;

            var myMovingMarker = L.Marker.movingMarker([[oldLat, oldLon],[lat, lon]],
                10000, {autostart: true});
        
            var greenIcon = L.icon({
                iconUrl: 'public/images/camion-pompier.png',
            });
        
            myMovingMarker.options.icon = greenIcon;
        
            map.addLayer(myMovingMarker);
            movingMarkerArray.push(myMovingMarker);
            markerCamionArray[idCamion]._latlng.lat = lat
            markerCamionArray[idCamion]._latlng.lat = lon
        //}
    //} // fin for

    //console.log(lon);

    /*var iconeCamion = L.icon({
        iconUrl: "public/images/camion-pompier.png",
        iconSize: [64, 64],
        iconAnchor: [0, 0],
        popupAnchor: [0, 0],
    });

    L.marker([lat, lon], { icon: iconeCamion }).addTo(map);*/
}

function receiveDataFromPython(exampleSocket, lon, lat, i, index){
        //ouverture de la socket
        /*exampleSocket.onopen = function(e) {};

        //récupération des données
        exampleSocket.onmessage = function(event) {
        var f = document.getElementById("chatbox").contentDocument;
        var text = "";
        var msg = JSON.parse(event.data);
        var time = new Date(msg.date);
        var timeStr = time.toLocaleTimeString();
        
        switch(msg.type) {
            case "id":
            clientID = msg.id;
            setUsername();
            break;
            case "username":
            text = "<b>User <em>" + msg.name + "</em> signed in at " + timeStr + "</b><br>";
            break;
            case "message":
            text = "(" + timeStr + ") <b>" + msg.name + "</b>: " + msg.text + "<br>";
            break;
            case "rejectusername":
            text = "<b>Your username has been set to <em>" + msg.name + "</em> because the name you chose is in use.</b><br>"
            break;
            case "userlist":
            var ul = "";
            for (i=0; i < msg.users.length; i++) {
                ul += msg.users[i] + "<br>";
            }
            document.getElementById("userlistbox").innerHTML = ul;
            break;
        }
        
        if (text.length) {
            f.write(text);
            document.getElementById("chatbox").contentWindow.scrollByPages(1);
        }
        };
    
        //fermeture dela connexion
        exampleSocket.close();*/
        
        i=i+0.002;
        return [idCamion, newLat, newLon] = [index, lat+i, lon+i];
}

function tableCreate() {
    var html = '<table class="table table-bordered">';
    var numero = 1;
    var keys_camions = Object.keys(camions);
    var nb_camions = keys_camions.length;
    for (var i = 0; i < nb_camions; i++){
        //if(etatCamion[i] == "Disponible"){
            html += '<tbody>'+
            '<tr id="ligne_'+numero+'" class="bg-success">'+
            '<th style="vertical-align: middle;" scope="row">'+ numero +'</th>'+
            '<td>'+
                '<img style="display: inline-block;" src="public/images/info_camion.jpg" alt="" border=3 height=100 width=100></img>'+
                '<div id="'+numero+'" style="display: inline-block;color: green;" style="color: green;">Etat du camion : <strong> Disponible </strong></div>'+
            '</td>'+
            '</tr>'+
            '</tbody>';
        numero++;
    }

    html += '</table>';

    document.getElementById("info").insertAdjacentHTML("beforeend", html);
}

window.onload = function(){
    var [lat, lon] = [45.750000, 4.850000];
    var i=0;
    feu_affiche = true;
    camion_affiche = true;

    document.getElementById("displayFire").addEventListener("click", displayFire);
    document.getElementById("displayTruck").addEventListener("click", displayTruck);

    // Fonction d'initialisation qui s'exécute lorsque le DOM est chargé
    initMap(lat, lon);

    //var exampleSocket = new WebSocket("ws://www.example.com/socketserver");
    var exampleSocket = '';

    keys_camions = Object.keys(camions);
    for(index=0;index<keys_camions.length;index++){
        i=i+0.005;
        var [idCamion, newLat, newLon] = receiveDataFromPython(exampleSocket, lon, lat, i, index);
        moveCamion(idCamion, newLon, newLat);
    }
    
    //i=i+0.02;
    
    tableCreate();
    document.getElementById("info").insertAdjacentHTML("beforeend", "<div class='copyright'> © Marine MOLLIER - Pierre-Louis BINARD - Enzo BALDISSERI - Thomas GIBAND </div>");

    window.setInterval(function(){
        checkEtatCamion();
    }, 1000);

    var keys_feux = Object.keys(incendies);
    var nb_feux = keys_feux.length;
    if(nb_feux!=0){
        document.getElementById("nb_incendie").insertAdjacentHTML("beforeend", nb_feux);
    }
};
