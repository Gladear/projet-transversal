import server.model.trucks as trucks
import server.model.sensors as sensors
import server.model.intervention as intervention
import server.ws.simulator as ws
import signal

def update_geolocation(data: dict):
    truck_id = data['id']
    geolocation = data['geolocation']

    trucks.update(truck_id, {
        'geolocation': geolocation,
    })

def update_available(data: dict):
    truck_id = data['id']

    trucks.update(truck_id, {
        'available': True,
    })

    print(f'Truck #{truck_id} is now available')

def send_truck(payload: dict):
    truck_id = payload['truck_id']
    sensor_id = payload['sensor_id']

    print(f'Sending truck #{truck_id} to sensor #{sensor_id}')

    sensor = sensors.get(sensor_id)
    geolocation = sensor['geolocation']

    truck = trucks.update(truck_id, {
        'available': False,
    })

    ws.send_truck(truck, geolocation)
    intervention.start(sensor_id, truck_id)

def end_all_intervention(signal_num, stack_frame):
    intervention.end_all()

signal.signal(signal.SIGUSR1, end_all_intervention)