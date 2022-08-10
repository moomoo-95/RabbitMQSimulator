package moomoo.rmq.simulator;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.service.ServiceManager;

@Slf4j
public class SimulatorMain {

    public static void main(String[] args) {
        if (args.length < 1) {
            log.error("Not enough parameters. [{}]", args.length);
            System.exit(1);
        }

        AppInstance appInstance = AppInstance.getInstance();
        appInstance.setConfig(args[0]);
        if (!appInstance.setResources()) return;

        ServiceManager.getInstance().loop();
    }
}
