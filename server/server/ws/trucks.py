from server import sockets
from geventwebsocket.websocket import WebSocket
import simplejson as json
import server.db as db

# Global variables
trucks = {}
websocket = None

# Routes
@sockets.route('/ws/trucks')
def handle_trucks(_websocket: WebSocket):
    global websocket
    websocket = _websocket

    try:
        while not websocket.closed:
            data = websocket.receive()
            msg = json.loads(data)
            handle_geolocation_update(msg)
    except Exception as error:
        websocket.close()
        websocket = None

        print(f'WebSocket Error on Trucks: {error}')

# Public functions
def handle_geolocation_update(data: dict):
    global trucks

    truck_id = data['id']
    lat, lon = data['lat'], data['lon']

    trucks[truck_id]['lat'] = lat
    trucks[truck_id]['lon'] = lon

def send_truck(payload: dict):
    global websocket

    if websocket is None:
        print('WebSocket Error on Simulator: Simulator is not connected')
        return

    truck_id = payload['id']
    geolocation = payload['geolocation']

    print(f'sending truck #{truck_id} to {geolocation}')

    trucks[truck_id]['available'] = False

    websocket.send(json.dumps({
        'action': 'send_truck',
        'payload': {
            'id': truck_id,
            'geolocation': geolocation
        },
    }))

# Initialize required data
truck_data = db.get_all("""
    SELECT truck.id,
        station.lat,
        station.lon
    FROM truck
        JOIN station
            ON truck.station_id = station.id
""")

for truck in truck_data:
    truck_id = truck['id']

    truck['available'] = True

    trucks[truck_id] = truck