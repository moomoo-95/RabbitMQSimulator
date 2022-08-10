package moomoo.rmq.simulator;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.config.UserConfig;
import moomoo.rmq.simulator.service.ServiceManager;
import moomoo.rmq.simulator.util.MsgParser;
import moomoo.rmq.simulator.util.XmlParser;

@Slf4j
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

    public boolean setResources() {
        return XmlParser.readVariableXmlFile() && MsgParser.readMsgDir() && XmlParser.readScenarioXmlFile();
    }
}
