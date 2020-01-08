import server.db as db

# Global variables
_trucks = {}

# Public functions
def get_all():
    return list(_trucks.values())

def get(truck_id: int) -> dict:
    return _trucks[truck_id]

def update(truck_id: int, fields: dict) -> dict:
    truck = _trucks[truck_id]

    for key, value in fields.items():
        truck[key] = value

    return truck

# Initialize required data
# _truck_data = db.get_all("""
#     SELECT truck.id,
#         station.lat,
#         station.lon
#     FROM truck
#         JOIN station
#             ON truck.station_id = station.id
# """)

# for truck in _truck_data:
#     truck_id = truck['id']
#     lat, lon = truck['lat'], truck['lon']

#     _trucks[truck_id] = {
#         'id': truck_id,
#         'geolocation': {
#             'lat': lat,
#             'lon': lon,
#         },
#         'available': True,
#     }
_trucks = {
    1: {
        'id': 1,
        'geolocation': {
            'lat': 45.782045,
            'lon': 4.876798,
        },
        'available': True,
    },
}