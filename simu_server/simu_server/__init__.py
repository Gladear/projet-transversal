from flask import Flask
import asyncio
import websockets

# Create Flask application
app = Flask(__name__)

# Create the Web Socket server
import simu_server.ws

