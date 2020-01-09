import server.db as db
from datetime import datetime

def start(sensor_id: int, truck_id: int):
    db.execute(
        """
            INSERT INTO intervention (sensor_id, truck_id, beginning)
            VALUES (%s, %s, %s)
        """,
        (sensor_id, truck_id, datetime.now())
    )

def end(sensor_id: int):
    db.execute(
        """
            UPDATE intervention
            SET ending = %s
            WHERE sensor_id = %s
                AND ending IS NULL
        """,
        (datetime.now(), sensor_id)
    )