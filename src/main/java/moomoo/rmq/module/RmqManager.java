package moomoo.rmq.module;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.config.UserConfig;

import java.util.concurrent.*;

@Slf4j
public class RmqManager {

    private static final class Singleton { private static final RmqManager INSTANCE = new RmqManager(); }

    private static UserConfig config = AppInstance.getInstance().getConfig();

    private final ExecutorService rmqExecutorService;
    private final ConcurrentHashMap<String , RmqClient> rmqClientMap;
    private final BlockingQueue<String> messageQueue;

    private RmqServer rmqServer;

    public RmqManager() {
        rmqExecutorService = Executors.newFixedThreadPool(config.getRmqThreadSize());
        rmqClientMap = new ConcurrentHashMap<>();
        messageQueue = new ArrayBlockingQueue<>(config.getRmqQueueSize());
    }

    public static RmqManager getInstance() {
        return Singleton.INSTANCE;
    }

    public void startRmq() {
        // thread run
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
        RmqClient rmqClient = new RmqClient(config.getRmqHost(), config.getRmqTargetQueue(), config.getRmqUser(), config.getRmqPass(), config.getRmqPort());
        if (rmqClient.start()) {
            rmqClientMap.put(config.getRmqTargetQueue(), rmqClient);
            log.debug("RabbitMQ Client Start Success. [{}], [{}], [{}]", config.getRmqTargetQueue(), config.getRmqHost(), config.getRmqUser());
        } else {
            log.debug("RabbitMQ Client Start Fail. [{}], [{}], [{}]", config.getRmqTargetQueue(), config.getRmqHost(), config.getRmqUser());
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
        if (!rmqClientMap.isEmpty()) {
            rmqClientMap.forEach((key, client) -> client.stop());
            rmqClientMap.clear();
        }
    }

    private void stopRmqConsumer() {
        if (!rmqExecutorService.isShutdown()) {
            rmqExecutorService.shutdown();
        }
    }
}
