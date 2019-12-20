import server.ws.emergency_manager as emergency_manager
import server.db as db

# Global variables
sensors = {}

# Public functions
def handle_sensor_data(data: dict):
    global sensors

    device_address = data['device_address']
    intensity = data['intensity']

    if device_address not in sensors:
        print(f"Device #{device_address} sent a message but isn't referenced in database")
        return

    sensor = sensors[device_address]

    if sensor['intensity'] != intensity:
        sensor['intensity'] = intensity

        emergency_manager.send_fire_update(sensor)

# Initialize sensors
sensor_data = db.get_all("""
    SELECT id, label, lat, lon
    FROM sensor
""")

for sensor in sensor_data:
    sensor['intensity'] = 0
    sensors[sensor['id']] = sensor