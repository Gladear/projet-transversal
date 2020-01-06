import server.db as db

# Global variables
trucks = {}

# Public functions
def handle_geolocation_update(data: dict):
    global trucks

    truck_id = data['id']
    lat, lon = data['lat'], data['lon']

    trucks[truck_id]['lat'] = lat
    trucks[truck_id]['lon'] = lon

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