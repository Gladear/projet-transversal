import server.db as db

# Global variables
_sensors = {}

# Public functions
def get_all() -> list:
    return list(_sensors.values())

def get(sensor_id: int) -> dict:
    return _sensors[sensor_id]

def exists(sensor_id: int) -> bool:
    return sensor_id in _sensors

def update(sensor_id: int, fields: dict) -> dict:
    sensor = get(sensor_id)

    for key, value in fields.items():
        sensor[key] = value

    return sensor

# Initialize required data
# _sensor_data = db.get_all("""
#     SELECT id, label, lat, lon
#     FROM sensor
# """)

# for sensor in _sensor_data:
#     sensor_id = sensor['id']

#     _sensors[sensor_id] = {
#         'id': sensor_id,
#         'geolocation': {
#             'lat': sensor['lat'],
#             'lon': sensor['lon'],
#         },
#         'intensity': 0,
#     }
_sensors = {
    1: {
        'id': 1,
        'geolocation': {
            'lat': 45.783222,
            'lon': 4.878086,
        },
        'intensity': 0,
    },
    2: {
        'id': 2,
        'geolocation': {
            'lat': 45.783151,
            'lon': 4.881199,
        },
        'intensity': 0,
    }
}