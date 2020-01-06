from server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json
import server.controller.simulator as controller
import server.ws.actions as actions

websocket = None

def dispatch_action(action: str, payload):
    if action == actions.ACTION_SEND_TRUCK:
        controller.send_truck(payload)
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
            'geolocation': sensor['geolocation'],
            'intensity': sensor['intensity'],
        },
    }))