from server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json
import server.controller.simulator as controller
import server.ws.actions as actions
import server.model.sensors as sensors
import server.model.trucks as trucks

clients = []

def dispatch_action(websocket: WebSocket, action: str, payload):
    print(f'/ws/client Received action {action} with payload {payload}')

    if action == actions.ACTION_GET_SENSORS:
        websocket.send(json.dumps({
            'action': actions.ACTION_SET_SENSORS,
            'payload': sensors.get_all(),
        }))
    else:
        print(f'Unknown action "{action}"')

# Routes
@sockets.route('/ws/client')
def handle_simulator(websocket: WebSocket):
    global clients

    clients.append(websocket)

    print('Connected to /ws/client')

    try:
        while not websocket.closed:
            data = websocket.receive()
            msg = json.loads(data)

            action = msg['action']
            payload = msg['payload']

            dispatch_action(websocket, action, payload)
    except Exception as error:
        websocket.close()

        clients.remove(websocket)
        websocket = None

        print(f'WebSocket Error on Simulator: {error}')


def _send_clients(data):
    global clients

    for client in clients:
        client.send(json.dumps(data))


def send_fire_update(sensor: dict):
    _send_clients({
        'action': actions.ACTION_SENSOR_UPDATE,
        'payload': sensor,
    })


def update_geolocation(truck: dict):
    _send_clients({
        'action': actions.ACTION_TRUCK_GEOLOCATION,
        'payload': truck,
    })
