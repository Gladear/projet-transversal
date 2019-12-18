from server import app
import server.db as db
import simplejson as json

@app.route('/api/sensors', methods=['GET'])
def get_sensors():
    sensors = db.get_all("""
        SELECT id, label, lat, lon
        FROM sensor
    """)

    return json.dumps(sensors)