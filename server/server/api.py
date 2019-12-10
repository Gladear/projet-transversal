from server import app

@app.route('/api/sensors', methods=['GET'])
def get_sensors():
    return {
        "sensors": [
            {
                "id": 0x01,
                "lat": 45.783386,
                "lon": 4.864920,
            },
            {
                "id": 0x02,
                "lat": 45.779439,
                "lon": 4.865912,
            },
            {
                "id": 0x03,
                "lat": 45.786719,
                "lon": 4.881763,
            },
            {
                "id": 0x04,
                "lat": 45.781908,
                "lon": 4.871804,
            },
            {
                "id": 0x05,
                "lat": 45.783436,
                "lon": 4.877360,
            },
            {
                "id": 0x06,
                "lat": 45.784383,
                "lon": 4.869151,
            },
        ],
    }