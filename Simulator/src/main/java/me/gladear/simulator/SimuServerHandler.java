package me.gladear.simulator;

import java.net.URI;

import me.gladear.simulator.comm.WebSocketClientEndpoint;
import me.gladear.simulator.utils.SensorHolder;
import me.gladear.simulator.utils.WSUtils;

class SimuServerHandler implements Runnable {
    private final String MSG_GET_DATA = "getDatas";
    private final String MSG_SEND_INTENSITY = "sendIntensity";

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
                if (MSG_GET_DATA.equals(message)) {
                    var sensors = this.sensors.getSensors();

                    var msg = WSUtils.createMessage(MSG_GET_DATA, sensors);
                    client.sendMessage(msg);
                }
            });

            var index = 0;
            var sensors = this.sensors.getSensors();

            while (true) {
                var msg = WSUtils.createMessage(MSG_SEND_INTENSITY, sensors[index]);
                client.sendMessage(msg);

                index += 1;

                if (index > sensors.length) {
                    index = 0;
                }

                try {
                    Thread.sleep(50);
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
