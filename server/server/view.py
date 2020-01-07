from server import app

@app.route('/')
def page_home():
    return app.send_static_file('map.html')