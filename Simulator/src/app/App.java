package app;

public class App {
  public static final long TICK_TIME = 1000l;

  public static void main(final String[] args) throws Exception {
    // # 1. Starts a thread that ignites fires
    var fireLiter = new Thread(new FireLiter());
    fireLiter.start();

    // # 2. Starts a thread that updates the geolocation
    // of all trucks every second (or more frequently).

    // When a fire is extinguished, the trucks assigned
    // to the fire come back to there stations

    // If a truck came back to it's station,
    // it is deleted
  }
}
