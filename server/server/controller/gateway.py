import server.model.sensors as sensors
import server.ws.emergency_manager as emergency_manager
import server.ws.client as client

# Public functions
def handle_sensor_data(data: dict):
    sensor_id = data['device_address']
    intensity = data['intensity']

    if not sensors.exists(sensor_id):
        print(f"Device #{sensor_id} sent a message but isn't referenced in database")
        return

    sensor = sensors.get(sensor_id)

    if sensor['intensity'] != intensity:
        sensor = sensors.update(sensor_id, {
            'intensity': intensity,
        })

        emergency_manager.send_fire_update(sensor)
        client.send_fire_update(sensor)

        if intensity == 0:
            intervention.end(sensor_id)
