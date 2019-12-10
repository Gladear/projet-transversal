package me.gladear.simulator;

import io.github.cdimascio.dotenv.Dotenv;

public class App {
  public static final long TICK_TIME = 1000l;
  public static final Dotenv dotenv = Dotenv.load();

  public static void main(final String[] args) throws Exception {
    // # 1. Starts a web socket connection

    // # 2. Starts a thread that ignites fires
    var fireLiter = new Thread(new FireLiter());
    fireLiter.start();

    // # 3. Handle trucks
  }
}
