package moomoo.rmq.simulator;

import moomoo.rmq.simulator.config.UserConfig;

public class AppInstance {

    private static final class Singleton { private static final AppInstance INSTANCE = new AppInstance(); }

    public static final String KEY = "rmqKey";
    public static final String ALGORITHM = "PBEWITHMD5ANDDES";

    private UserConfig config;

    public static AppInstance getInstance() {
        return Singleton.INSTANCE;
    }

    public AppInstance() {
        // nothing
    }

    public UserConfig getConfig() {
        return config;
    }

    public void setConfig(String configPath) {
        this.config = new UserConfig(configPath);
    }
}
