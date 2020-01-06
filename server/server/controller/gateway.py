import server.model.sensors as sensors
import server.ws.emergency_manager as emergency_manager

# Public functions
def handle_sensor_data(data: dict):
    device_address = data['device_address']
    intensity = data['intensity']

    if not sensors.exists(device_address):
        print(f"Device #{device_address} sent a message but isn't referenced in database")
        return

    sensor = sensors.get(device_address)

    if sensor['intensity'] != intensity:
        sensor = sensors.update(device_address, {
            'intensity': intensity,
        })

        emergency_manager.send_fire_update(sensor)
