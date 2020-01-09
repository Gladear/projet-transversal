#/bin/bash

pids=()

cd Simulator
echo "Compiling Simulator..."
mvn compile
cd -

cd EmergencyManager
echo "Compiling Emergency Manager..."
mvn compile
cd -

cd server
echo "Launcher server..."
gunicorn -b 127.0.0.1:5000 -k flask_sockets.worker server:app &
pids+=( $! )
cd -

cd simu_server
echo "Launcher simulation server..."
gunicorn -b 127.0.0.1:4000 -k flask_sockets.worker simu_server:app &
pids+=( $! )
cd -

cd Simulator
echo "Launcher Simulator..."
mvn exec:java -Dexec.mainClass=me.gladear.simulator.App &
pids+=( $! )
cd -

cd EmergencyManager
echo "Launching Emergency Manager..."
mvn exec:java -Dexec.mainClass=me.gladear.emergencymanager.App &
pids+=( $! )
cd -

sleep 3

echo "Program initialized"
read -p "Press any key to shut down the program..."

for pid in ${pids[@]}; do
	kill $pid
done

sleep 3
echo "Good bye !"
