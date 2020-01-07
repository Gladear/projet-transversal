from simu_server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json
import simu_server.serial as serial

ACTION_SEND_ON_RF = u'send_on_rf'

def dispatch_action(action: str, payload: dict):
    if action == ACTION_SEND_ON_RF:
        serial.send(payload)
    else:
        print(f'Unknown action "{action}"')

@sockets.route('/ws/simulator')
def handle_send_on_rf(websocket: WebSocket):
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
        print(error)
