package moomoo.rmq.rmqif;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.module.RmqClient;
import moomoo.rmq.rmqif.module.RmqConsumer;
import moomoo.rmq.rmqif.module.RmqServer;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.config.UserConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class RmqManager {

    private static final class Singleton { private static final RmqManager INSTANCE = new RmqManager(); }

    private static UserConfig config = AppInstance.getInstance().getConfig();

    private final ExecutorService rmqExecutorService;
    private final BlockingQueue<String> messageQueue;

    private RmqServer rmqServer;
    private RmqClient rmqClient;

    public RmqManager() {
        rmqExecutorService = Executors.newFixedThreadPool(config.getRmqThreadSize());
        messageQueue = new ArrayBlockingQueue<>(config.getRmqQueueSize());
    }

    public static RmqManager getInstance() {
        return Singleton.INSTANCE;
    }

    public void startRmq() {
        startRmqServer();
        startRmqClient();
        startRmqConsumer();
    }

    public void stopRmq() {
        stopRmqServer();
        stopRmqClient();
        stopRmqConsumer();
    }


    // RabbitMQ Server 시작
    private void startRmqServer() {
        rmqServer = new RmqServer(config.getRmqHost(), config.getRmqLocalQueue(), config.getRmqUser(), config.getRmqPass(), config.getRmqPort(), messageQueue);
        if (rmqServer.start()) {
            log.debug("Rabbit MQ Server Start Success. [{}], [{}], [{}]", config.getRmqLocalQueue(), config.getRmqHost(), config.getRmqUser());
        } else {
            log.debug("Rabbit MQ Server Start Fail. [{}], [{}], [{}]", config.getRmqLocalQueue(), config.getRmqHost(), config.getRmqUser());
        }
    }

    // RabbitMQ Client 시작
    private void startRmqClient() {
        // AWF Client
        rmqClient = new RmqClient(config.getRmqHost(), config.getRmqTargetQueue(), config.getRmqUser(), config.getRmqPass(), config.getRmqPort());
        if (rmqClient.start()) {
            log.debug("Rabbit MQ Client Start Success. [{}], [{}], [{}]", config.getRmqTargetQueue(), config.getRmqHost(), config.getRmqUser());
        } else {
            log.debug("Rabbit MQ Client Start Fail. [{}], [{}], [{}]", config.getRmqTargetQueue(), config.getRmqHost(), config.getRmqUser());
        }
    }

    // RabbitMQ Consumer 시작
    private void startRmqConsumer() {
        for (int i = 0; i < config.getRmqThreadSize(); i++) {
            rmqExecutorService.execute(new RmqConsumer(messageQueue));
        }
    }

    private void stopRmqServer() {
        if (rmqServer != null) {
            rmqServer.stop();
            rmqServer = null;
        }
    }

    private void stopRmqClient() {
        if (rmqClient != null) {
            rmqClient.stop();
            rmqClient = null;
        }
    }

    private void stopRmqConsumer() {
        if (!rmqExecutorService.isShutdown()) {
            rmqExecutorService.shutdown();
        }
    }

    public RmqClient getRmqClient() {
        return rmqClient;
    }
}
