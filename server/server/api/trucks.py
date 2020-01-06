from server import app
import server.db as db
import simplejson as json
from server.controller.trucks import trucks

@app.route('/api/trucks', methods=['GET'])
def get_trucks():
    return json.dumps(list(trucks.values()))