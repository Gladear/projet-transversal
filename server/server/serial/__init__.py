import serial
import threading
import simplejson as json

SERIALPORT = "/dev/ttyUSB1"
BAUDRATE = 115200

def handle_data(read: str):
    obj = json.loads(read)
    print(obj)

def handle_port(ser: serial.Serial):
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

    try:
        ser.open()
    except serial.SerialException:
        print("Serial {} port not available".format(SERIALPORT))
        exit()

    while True:
        read = ser.readline().decode()
        handle_data(read)

thread = threading.Thread(target=handle_port, args=(SERIALPORT,))
thread.start()


