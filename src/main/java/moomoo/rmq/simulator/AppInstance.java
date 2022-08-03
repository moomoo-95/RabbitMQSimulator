package moomoo.rmq.simulator;

public class AppInstance {

    private static final class Singleton {
        private static final AppInstance INSTANCE = new AppInstance();
    }

    public static AppInstance getInstance() {
        return Singleton.INSTANCE;
    }

    public AppInstance() {
        // nothing
    }
}
