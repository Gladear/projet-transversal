from server import sockets
from geventwebsocket.websocket import WebSocket

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
