from server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json

@sockets.route('/ws/trucks')
def handle_trucks(websocket: WebSocket):
    try:
        while not websocket.closed:
            msg = websocket.receive()
            print(msg)
    except Exception as error:
        websocket.close()
        print(error)
