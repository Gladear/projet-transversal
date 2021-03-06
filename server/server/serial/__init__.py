import serial
import server.controller.gateway as gateway
import simplejson as json
import threading

# SERIALPORT = "/dev/ttyUSB1"
# BAUDRATE = 115200

def handle_data(read: str):
    try:
        data = json.loads(read)
        gateway.handle_sensor_data(data)
    except:
        pass

# def handle_port(ser: serial.Serial):
#     ser = serial.Serial()
#     ser.port = SERIALPORT
#     ser.baudrate = BAUDRATE
#     ser.bytesize = serial.EIGHTBITS  # number of bits per bytes
#     ser.parity = serial.PARITY_NONE  # set parity check: no parity
#     ser.stopbits = serial.STOPBITS_ONE  # number of stop bits
#     ser.timeout = None  # block read

#     ser.xonxoff = False  # disable software flow control
#     ser.rtscts = False  # disable hardware (RTS/CTS) flow control
#     ser.dsrdtr = False  # disable hardware (DSR/DTR) flow control
#     # ser.writeTimeout = 0     #timeout for write

#     try:
#         ser.open()
#     except serial.SerialException:
#         print("Serial {} port not available".format(SERIALPORT))
#         exit()

#     while True:
#         read = ser.readline().decode()
#         handle_data(read)

# thread = threading.Thread(target=handle_port, args=(SERIALPORT,))
# thread.start()

from server import sockets
from geventwebsocket.websocket import WebSocket

@sockets.route('/ws/simu_server')
def handle_simu_server(_websocket: WebSocket):
    global websocket
    websocket = _websocket

    try:
        while not websocket.closed:
            data = websocket.receive()
            if data is not None:
                handle_data(data)

    except Exception as error:
        websocket.close()
        print(error)
