import os
from psycopg2 import pool, extras

pool = pool.SimpleConnectionPool(
    minconn=0,
    maxconn=4,
    host=os.getenv('DB_HOST', '127.0.0.1'),
    port=os.getenv('DB_PORT', '5432'),
    database=os.getenv('DB_DATABASE'),
    user=os.getenv('DB_USER'),
    password=os.getenv('DB_PASSWORD')
)

def dict_cursor():
    conn = pool.getconn()
    return conn.cursor(cursor_factory=extras.RealDictCursor)

def get_all(request):
    cur = dict_cursor()
    cur.execute(request)

    return cur.fetchall()

def close():
    pool.closeall()
