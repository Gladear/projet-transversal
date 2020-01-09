import server.model.trucks as model
import server.ws.simulator as ws

def update_geolocation(data: dict):
    truck_id = data['id']
    geolocation = data['geolocation']

    model.update(truck_id, {
        'geolocation': geolocation,
    })

def update_available(data: dict):
    truck_id = data['id']

    model.update(truck_id, {
        'available': True,
    })

    print(f'Truck #{truck_id} is now available')

def send_truck(payload: dict):
    truck_id = payload['truck_id']
    sensor_id = payload['sensor_id']

    print(f'Sending truck #{truck_id} to sensor #{sensor_id}')

    sensor = sensors.get(sensor_id)
    geolocation = sensor['geolocation']

    truck = model.update(truck_id, {
        'available': False,
    })

    ws.send_truck(truck, geolocation)
