package moomoo.rmq.rmqif.module;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.module.transport.RmqSender;
import moomoo.rmq.rmqif.module.util.PasswordEncryptor;
import moomoo.rmq.simulator.AppInstance;

@Slf4j
public class RmqClient {
    private final String host;
    private final String queueName;
    private final String userName;
    private final String password;
    private final int port;

    private RmqSender rmqSender;

    private boolean isConnected = false;

    public RmqClient(String host, String queueName, String userName, String password, int port) {
        this.host = host;
        this.queueName = queueName;
        this.userName = userName;
        this.password = password;
        this.port = port;
    }

    /**
     * 암호화된 password 를 복호화하여 RabbitMQ Server 에 연결
     */
    public boolean start() {
        PasswordEncryptor decryptor = new PasswordEncryptor(AppInstance.KEY, AppInstance.ALGORITHM);
        String decPass = "";

        try {
            decPass = decryptor.decrypt(password);
        } catch (Exception e) {
            log.error("RMQ Password is not available", e);
        }

        rmqSender = new RmqSender(host, queueName, userName, decPass, port);

        if (rmqSender.connect(false)) {
            isConnected = true;
        }
        return isConnected;
    }

    public void stop() {
        if (rmqSender != null) {
            rmqSender.disconnect();
            rmqSender = null;
        }
    }

    public boolean send(String msg) {
        if (rmqSender == null) {
            start();
            if (rmqSender == null) return false;
        }

        if (!rmqSender.isConnected()) return false;

        return rmqSender.send(msg);
    }
}
