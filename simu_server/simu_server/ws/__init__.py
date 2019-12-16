import asyncio
import websockets
import simplejson as json
import simu_server.serial as serial

MSG_SEND_INTENSITY = u'send_on_rf'

def dispatch_action(action: str, payload):
    if action == MSG_SEND_INTENSITY:
        serial.send(payload)
    else:
        print(f'Unknown action "{action}"')

async def comm_simulator(websocket: websockets.server.WebSocketServerProtocol, path: str):
    try:
        async for message in websocket:
            msg = json.loads(message)
            action = msg['action']
            payload = msg['payload']

            dispatch_action(action, payload)
    except Exception as error:
        websocket.close(reason="an error occured")
        print(error)

print('Initializing Web Socket server...')

asyncio.get_event_loop().run_until_complete(
    websockets.serve(comm_simulator, 'localhost', 4001))

print('Initialized Web Socket server on localhost:4001')

asyncio.get_event_loop().run_forever()