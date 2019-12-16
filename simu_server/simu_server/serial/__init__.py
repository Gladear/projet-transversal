import serial

SERIALPORT = "/dev/ttyUSB0"
BAUDRATE = 115200

ser = None

def init():
    ser = serial.Serial()
    ser.port = SERIALPORT
    ser.baudrate = BAUDRATE
    ser.bytesize = serial.EIGHTBITS  # number of bits per bytes
    ser.parity = serial.PARITY_NONE  # set parity check: no parity
    ser.stopbits = serial.STOPBITS_ONE  # number of stop bits
    ser.timeout = None  # block read

    ser.xonxoff = False  # disable software flow control
    ser.rtscts = False  # disable hardware (RTS/CTS) flow control
    ser.dsrdtr = False  # disable hardware (DSR/DTR) flow control
    # ser.writeTimeout = 0     #timeout for write
    print("Starting Up Serial Monitor")

    try:
        ser.open()
    except serial.SerialException:
        print("Serial {} port not available".format(SERIALPORT))
        exit()

def send(payload):
    # if ser is None:
    #     init()

    data = int(payload['id']).to_bytes(4, byteorder='little') + int(payload['intensity']).to_bytes(4, byteorder='little')
    print(f'payload: {payload}, bytes: {data}')
    # ser.write(data)
