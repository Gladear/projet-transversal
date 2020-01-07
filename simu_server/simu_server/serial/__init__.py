# import serial

# SERIALPORT = "/dev/ttyUSB0"
# BAUDRATE = 115200

# ser = None

# def init():
#     global ser
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
#     print("Starting Up Serial Monitor")

#     try:
#         ser.open()
#     except serial.SerialException:
#         print("Serial {} port not available".format(SERIALPORT))
#         exit()

# def send(payload: dict):
#     global ser
#     if ser is None:
#         init()

#     data = int(payload['id']).to_bytes(4, byteorder='little') + int(payload['intensity']).to_bytes(4, byteorder='little')

#     ser.write(data)

import simplejson as json
import websocket

try:
    client = websocket.create_connection('ws://127.0.0.1:5000/ws/simu_server')
except Exception as error:
    print(f'WS Client Error: {error}')
    exit()

def send(payload: dict):
    client.send(json.dumps({
        'device_address': payload['id'],
        'intensity': payload['intensity'],
    }))