import server.model.trucks as model
import server.ws.simulator as ws

def update_geolocation(data: dict):
    truck_id = data['id']
    geolocation = data['geolocation']

    model.update(truck_id, {
        'geolocation': geolocation,
    })

    print(f'Truck #{truck_id} is now at {geolocation}')

def send_truck(payload: dict):
    truck_id = payload['id']
    geolocation = payload['geolocation']

    print(f'Sending truck #{truck_id} to {geolocation}')

    truck = model.update(truck_id, {
        'available': False,
    })

    ws.send_truck(truck, geolocation)
