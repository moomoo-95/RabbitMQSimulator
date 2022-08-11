package moomoo.rmq.rmqif.module.transport;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.module.transport.base.RmqCallback;
import moomoo.rmq.rmqif.module.transport.base.RmqTransport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * Rabbit MQ Broker 내 특정 Queue 에 메시지를 가져오는 클래스
 */
@Slf4j
public class RmqReceiver extends RmqTransport {

    private RmqCallback callback = null;
    private final Consumer consumer;

    public RmqReceiver(String host, String queueName, String userName, String password, int port) {
        super(host, queueName, userName, password, port);

        this.consumer = new DefaultConsumer(getChannel()) {
            @Override
            // 메시지가 큐에 적재되면 호출되는 메서드
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, StandardCharsets.UTF_8);
                if ( callback != null ) {
                    try {
                        Date ts = null;
                        Map<String, Object> headers = properties.getHeaders();
                        if (headers != null) {
                            Long ms = (Long)headers.get("timestamp_in_ms");
                            if (ms != null) {
                                ts = new Date(ms);
                            }
                        }
                        callback.onReceived( msg, ts );
                    }
                    catch (Exception e) {
                        log.error("RmqReceiver.handleDelivery ",e);
                    }
                }
            }
        };
    }

    public boolean setReceive() {
        if (!this.isConnected()) {
            log.warn("RMQ channel is not opened");
            return false;
        }

        try {
            this.getChannel().basicConsume(getQueueName(), true, this.consumer);
            return true;
        } catch (Exception e) {
            log.error("RmqReceiver.start", e);
            return false;
        }
    }

    public void setCallback(RmqCallback callback) {
        this.callback = callback;
    }
}
