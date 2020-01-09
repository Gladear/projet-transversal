#/bin/bash

cd server
echo "Launcher server..."
gunicorn -b 127.0.0.1:5000 -k flask_sockets.worker server:app &
cd -

cd simu_server
echo "Launcher simulation server..."
gunicorn -b 127.0.0.1:4000 -k flask_sockets.worker simu_server:app &
cd -

cd Simulator
echo "Compiling Simulator..."
mvn compile
echo "Launcher Simulator..."
mvn exec:java -Dexec.mainClass=me.gladear.simulator.App &
cd -

cd EmergencyManager
echo "Compiling Emergency Manager..."
mvn compile
echo "Launching Emergency Manager..."
mvn exec:java -Dexec.mainClass=me.gladear.emergencymanager.App &
cd -
