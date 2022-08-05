package moomoo.rmq.rmqif.module.transport;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.module.transport.base.RmqTransport;

import java.nio.charset.Charset;

/**
 * Rabbit MQ Broker 내 특정 Queue 에 메시지를 전송하는 클래스
 */
@Slf4j
public class RmqSender extends RmqTransport {

    public RmqSender(String host, String queueName, String userName, String password, int port) {
        super(host, queueName, userName, password, port);
    }

    /**
     * byte array 로 변환한 길이와 이전 길이가 다르면 전송하지 않음
     * todo 확인 필요
     * @param msg String 을 byte 배열로 변환한 값
     * @param size 기존 String 의 길이 값
     * @return
     */
    private boolean send(byte[] msg, int size) {
        if (!this.isConnected()) {
            log.warn("RMQ channel is not opened");
            return false;
        }

        if ((size <= 0) || (msg == null) || (msg.length < size)) {
            log.warn("Send error: wrong param. size [{}] msg [{}]", size, (msg != null) ? msg.length : 0);
            return false;
        }

        try {
            this.getChannel().basicPublish("", this.getQueueName(), null, msg);
            return true;
        } catch (Exception e) {
            log.error("RmqSender.send ", e);
            return false;
        }
    }

    public boolean send(String msg) {
        return send(msg.getBytes(Charset.defaultCharset()), msg.length());
    }
}
