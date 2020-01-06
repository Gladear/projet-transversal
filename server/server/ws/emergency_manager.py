from server import sockets
from geventwebsocket.websocket import WebSocket
from server.ws.trucks import send_truck
import simplejson as json

ACTION_SEND_TRUCK = u'send_truck'

websocket = None

def dispatch_action(action: str, payload):
    if action == ACTION_SEND_TRUCK:
        send_truck(payload)
    else:
        print(f'Unknown action "{action}"')

@sockets.route('/ws/emergency_manager')
def handle_emergency_manager(_websocket: WebSocket):
    global websocket
    websocket = _websocket

    try:
        while not websocket.closed:
            data = websocket.receive()
            msg = json.loads(data)
            
            action = msg['action']
            payload = msg['payload']

            dispatch_action(action, payload)
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