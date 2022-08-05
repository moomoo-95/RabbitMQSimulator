package moomoo.rmq.rmqif.module;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.module.transport.RmqReceiver;
import moomoo.rmq.rmqif.module.transport.base.RmqCallback;
import moomoo.rmq.rmqif.module.util.PasswordEncryptor;
import moomoo.rmq.simulator.AppInstance;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class RmqServer {
    private final String host;
    private final String queueName;
    private final String userName;
    private final String password;
    private final int port;

    private final BlockingQueue<String> queue;

    private RmqReceiver rmqReceiver;

    public RmqServer(String host, String queueName, String userName, String password, int port, BlockingQueue<String> queue) {
        this.host = host;
        this.queueName = queueName;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.queue = queue;
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

        rmqReceiver = new RmqReceiver(host, queueName, userName, decPass, port);
        rmqReceiver.setCallback(new MessageCallback());

        if (rmqReceiver.connect(true)) {
            return rmqReceiver.receiveStart();
        }
        return false;
    }

    public void stop() {
        if (rmqReceiver != null) {
            rmqReceiver.receiveStop();
            rmqReceiver = null;
        }
    }

    /**
     * AMF 에서 A2S 큐에 보낸 메세지에 대해 로그 출력, MQ queue 에 put
     */
    private class MessageCallback implements RmqCallback {
        @Override
        public void onReceived(String msg, Date ts) {

            // todo msg 가 설정한 메시지 형식과 일치하는지 검증
            try {
                queue.put(msg);
            } catch (Exception e) {
                log.error("MessageCallback.onReceived", e);
                Thread.currentThread().interrupt();
            }

        }
    }
}
