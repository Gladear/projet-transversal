package me.gladear.simulator;

import java.net.URI;

import me.gladear.simulator.comm.WebSocketClientEndpoint;
import me.gladear.simulator.utils.SensorHolder;
import me.gladear.simulator.utils.WSUtils;

class SimuServerHandler implements Runnable {
    private final String ACTION_GET_SENSORS = "get_sensors";
    private final String ACTION_SEND_INTENSITY = "send_on_rf";

    private final SensorHolder sensors;

    public SimuServerHandler(SensorHolder sensors) {
        this.sensors = sensors;
    }

    @Override
    public void run() {
        try {
            var url = new URI(App.dotenv.get("SIMU_SERVER_WS"));
            var client = new WebSocketClientEndpoint(url);

            client.addMessageHandler(message -> {
                if (ACTION_GET_SENSORS.equals(message)) {
                    var sensors = this.sensors.getSensors();
                    var msg = WSUtils.createMessage(ACTION_GET_SENSORS, sensors);

                    client.sendMessage(msg);
                }
            });

            var index = 0;
            var sensors = this.sensors.getSensors();

            while (client.isOpen()) {
                var sensor = sensors[index];
                var msg = WSUtils.createMessage(ACTION_SEND_INTENSITY, sensor);

                client.sendMessage(msg);

                index += 1;

                if (index >= sensors.length) {
                    index = 0;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Whathever, messages will be sent
                    // sooner than expected ¯\_(ツ)_/¯
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
