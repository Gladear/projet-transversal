import server.db as db

# Global variables
_trucks = {}

# Public functions
def get_all():
    return list(_trucks.values())

def get_in_action():
    return list(filter(lambda truck: not truck['available'], get_all()))

def get(truck_id: int) -> dict:
    return _trucks[truck_id]

def update(truck_id: int, fields: dict) -> dict:
    truck = _trucks[truck_id]

    for key, value in fields.items():
        truck[key] = value

    return truck

# Initialize required data
_truck_data = db.get_all("""
    SELECT truck.id,
        truck.capacity,
        station.lat,
        station.lon,
        intervention.id intervention_id
    FROM truck
        JOIN station
            ON truck.station_id = station.id
        LEFT JOIN intervention
            ON truck_id = truck.id
            AND intervention.ending IS NULL
""")

for truck in _truck_data:
    truck_id = truck['id']
    capacity = truck['capacity']
    intervention_id = truck['intervention_id']
    lat, lon = truck['lat'], truck['lon']

    _trucks[truck_id] = {
        'id': truck_id,
        'capacity': capacity,
        'geolocation': {
            'lat': lat,
            'lon': lon,
        },
        'available': intervention_id == None,
    }
