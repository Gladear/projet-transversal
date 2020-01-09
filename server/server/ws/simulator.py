from server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json
import server.controller.simulator as controller
import server.ws.actions as actions
import server.ws.client as client
import server.model.sensors as sensors

# Global variables
websocket = None

def dispatch_action(action: str, payload):
    if action == actions.ACTION_TRUCK_GEOLOCATION:
        controller.update_geolocation(payload)
        client.update_geolocation(payload)
    elif action == actions.ACTION_TRUCK_AVAILABLE:
        controller.update_available(payload)
    else:
        print(f'Unknown action "{action}"')

# Routes
@sockets.route('/ws/simulator')
def handle_simulator(_websocket: WebSocket):
    global websocket
    websocket = _websocket

    print('Connected to /ws/simulator')

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

        print(f'WebSocket Error on Simulator: {error}')

def send_truck(truck: dict, geolocation: dict):
    global websocket

    if websocket is None:
        print('WebSocket Error on Simulator: Simulator is not connected')
        return

    websocket.send(json.dumps({
        'action': actions.ACTION_SEND_TRUCK,
        'payload': {
            'id': truck['id'],
            'from': truck['geolocation'],
            'to': geolocation,
        },
    }))
