from server import app
import server.db as db
import simplejson as json
import server.model.trucks as model

@app.route('/api/trucks', methods=['GET'])
def get_trucks():
    return json.dumps(model.get_all())