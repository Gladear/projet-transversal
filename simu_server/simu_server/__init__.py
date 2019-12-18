from flask import Flask
from flask_sockets import Sockets

# Create Flask application
app = Flask(__name__)
sockets = Sockets(app)

# Create the Web Socket server
import simu_server.ws

if __name__ == "__main__":
    from gevent import pywsgi
    from geventwebsocket.handler import WebSocketHandler
    server = pywsgi.WSGIServer(('', 4000), app, handler_class=WebSocketHandler)
    server.serve_forever()
