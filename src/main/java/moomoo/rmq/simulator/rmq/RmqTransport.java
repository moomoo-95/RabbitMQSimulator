package moomoo.rmq.simulator.rmq;

import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RmqTransport {
    private final String host;
    private final String queueName;
    private final String userName;
    private final String password;
    private final int port;

    private Connection connection;
    private Channel channel;

    public RmqTransport(String host, String queueName, String userName, String password, int port) {
        this.host = host;
        this.queueName = queueName;
        this.userName = userName;
        this.password = password;
        this.port = port;
    }

    /**
     * RabbitMQ broker 와 connection 을 맺기 위한 메서드
     */
    private boolean makeConnection() {
        // 이미 채널이 열려있는 경우 connection 상태
        if (this.channel != null && this.channel.isOpen()) {
            return true;
        }
        ConnectionFactory factory = new ConnectionFactory();
        // 연결할 rabbitmq 서버 설정
        factory.setHost(this.host);
        factory.setUsername(this.userName);
        factory.setPassword(this.password);
        factory.setPort(this.port);
        // 기타 rabbitmq 옵션 설정
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(1000);
        factory.setRequestedHeartbeat(5);
        factory.setConnectionTimeout(2000);
        factory.setExceptionHandler(new DefaultExceptionHandler() {
            @Override
            public void handleUnexpectedConnectionDriverException(Connection con, Throwable exception) {
                log.error("handleUnexpectedConnectionDriverException {}", con.getAddress().toString(), exception);
            }

            @Override
            public void handleConnectionRecoveryException(Connection conn, Throwable exception) {
                log.error("handleConnectionRecoveryException {}", conn.getAddress().toString(), exception);
            }

            @Override
            public void handleChannelRecoveryException(Channel ch, Throwable exception) {
                log.error("handleChannelRecoveryException {}", ch.getConnection().getAddress().toString(), exception);
            }
        });

        try {
            this.connection = factory.newConnection(this.queueName);
            this.connection.addBlockedListener(new BlockedListener() {
                @Override
                public void handleBlocked(String reason) {
                    log.error("handleBlocked {}", reason);
                }

                @Override
                public void handleUnblocked() {
                    log.error("handleUnBlocked");
                }
            });
            return true;
        } catch (Exception e) {
            log.error("RmqTransport.makeConnection, host : [{}], port [{}], user [{}], queueName [{}] ", this.host, this.port, this.userName, this.queueName, e);
            return false;
        }
    }
}
