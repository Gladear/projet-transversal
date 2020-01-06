from server import app
import server.db as db
import simplejson as json
import server.model.sensors as model

@app.route('/api/sensors', methods=['GET'])
def get_sensors():
    return json.dumps(model.get_all())