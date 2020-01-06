from server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json
import server.controller.trucks as trucks

websocket = None

@sockets.route('/ws/trucks')
def handle_trucks(_websocket: WebSocket):
    global websocket
    websocket = _websocket

    try:
        while not websocket.closed:
            data = websocket.receive()
            msg = json.loads(data)
            trucks.handle_geolocation_update(msg)
    except Exception as error:
        websocket.close()
        websocket = None

        print(f'WebSocket Error on Trucks: {error}')
