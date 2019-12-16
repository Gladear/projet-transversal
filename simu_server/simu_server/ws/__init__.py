import asyncio
import websockets

async def comm_simulator(websocket: websockets.server.WebSocketServerProtocol, path: str):
    async for message in websocket:
        print(message)
        await websocket.send(message)

print('Initializing Web Socket server...')

asyncio.get_event_loop().run_until_complete(
    websockets.serve(comm_simulator, 'localhost', 4001))

print('Initialized Web Socket server on localhost:4001')

asyncio.get_event_loop().run_forever()