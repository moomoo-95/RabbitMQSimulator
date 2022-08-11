package moomoo.rmq.rmqif.module;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.module.transport.RmqCallback;
import moomoo.rmq.rmqif.module.transport.RmqTransport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class RmqServer extends RmqTransport {

    private final BlockingQueue<String> queue;
    private final RmqCallback callback;
    private final Consumer consumer;

    public RmqServer(String host, String queueName, String userName, String password, int port, BlockingQueue<String> queue) {
        super(host, queueName, userName, password, port);

        this.callback = new MessageCallback();
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

        this.queue = queue;
    }

    /**
     * 암호화된 password 를 복호화하여 RabbitMQ Server 에 연결
     */
    public boolean start() {
        if (connect(true)) {
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
        return false;
    }

    public void stop() {
        disconnect();
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
