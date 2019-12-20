from server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json

websocket = None

@sockets.route('/ws/emergency_manager')
def handle_emergency_manager(_websocket: WebSocket):
    global websocket
    websocket = _websocket

    try:
        while not websocket.closed:
            msg = websocket.receive()
            print(msg)
    except Exception as error:
        websocket.close()
        websocket = None

        print(f'WebSocket Error on Emergency Manager: {error}')

def send_fire_update(sensor: dict):
    global websocket

    if websocket is None:
        print('WebSocket Error on Emergency Manager: Emergency Manager is not connected')
        return

    websocket.send(json.dumps({
        'action': 'fire_update',
        'payload': {
            'id': sensor['id'],
            'lat': sensor['lat'],
            'lon': sensor['lon'],
            'intensity': sensor['intensity'],
        },
    }))