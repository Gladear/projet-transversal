from flask import Flask
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Create Flask application
app = Flask(__name__)

# Load application module
import server.api