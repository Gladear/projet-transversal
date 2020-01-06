from server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json
import server.controller.simulator as controller
import server.ws.actions as actions

# Global variables
websocket = None

# Routes
@sockets.route('/ws/simulator')
def handle_simulator(_websocket: WebSocket):
    global websocket
    websocket = _websocket

    try:
        while not websocket.closed:
            data = websocket.receive()
            msg = json.loads(data)
            controller.update_geolocation(msg)
    except Exception as error:
        websocket.close()
        websocket = None

        print(f'WebSocket Error on Trucks: {error}')

def send_truck(truck_id: int, geolocation: dict):
    global websocket

    if websocket is None:
        print('WebSocket Error on Simulator: Simulator is not connected')
        return

    websocket.send(json.dumps({
        'action': actions.ACTION_SEND_TRUCK,
        'payload': {
            'id': truck_id,
            'geolocation': geolocation,
        },
    }))