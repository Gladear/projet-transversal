package me.gladear.emergencymanager;

import java.net.URI;

import io.github.cdimascio.dotenv.Dotenv;
import me.gladear.emergencymanager.comm.WebSocketClientEndpoint;

public final class App {
    public static final Dotenv dotenv = Dotenv.load();

    private App() {
        try {
            var uri = new URI(App.dotenv.get("SERVER_WS"));
            var client = new WebSocketClientEndpoint(uri);

            client.addMessageHandler(new Manager(client));

            while (client.isOpen()) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    // Pass
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) throws Exception {
        new App();
    }
}
