import server.db as db
from datetime import datetime

def start(sensor_id: int, truck_id: int):
    db.execute(
        """
            INSERT INTO intervention (sensor_id, truck_id, beginning)
            VALUES (%s, %s, current_timestamp)
        """,
        (sensor_id, truck_id)
    )

def end(sensor_id: int):
    db.execute(
        """
            UPDATE intervention
            SET ending = current_timestamp
            WHERE sensor_id = %s
                AND ending IS NULL
        """,
        (sensor_id)
    )
