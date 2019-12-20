from flask import Flask
from flask_sockets import Sockets
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Create Flask application
app = Flask(__name__)
sockets = Sockets(app)

# Load application module
import server.api
import server.ws
import server.serial


if __name__ == "__main__":
    from gevent import pywsgi
    from geventwebsocket.handler import WebSocketHandler
    server = pywsgi.WSGIServer(('', 5000), app, handler_class=WebSocketHandler)
    server.serve_forever()
