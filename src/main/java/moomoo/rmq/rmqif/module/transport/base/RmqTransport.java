package moomoo.rmq.rmqif.module.transport.base;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.config.UserConfig;

/**
 * RMQ에 연결하고 recovery, block 등을 관리한다.
 */
@Slf4j
public class RmqTransport {
    private final String host;
    private final String queueName;
    private final String userName;
    private final String password;
    private final int port;

    private Connection connection;
    private Channel channel;

    private String connectionName;

    public RmqTransport(String host, String queueName, String userName, String password, int port) {
        this.host = host;
        this.queueName = queueName;
        this.userName = userName;
        this.password = password;
        this.port = port;
    }

    /**
     * rmq 브로커에 연결하기 위한 메서드
     */
    public boolean connect(boolean isConsumer) {
        connectionName = String.format(isConsumer ? "Consumer_%s" : "Producer_%s" , queueName);
        if (!openConnection(connectionName)) {
            log.warn("[RMQ name: {}] Failed to open RMQ connection", connectionName);
            return false;
        } else if (!openChannel(isConsumer)) {
            log.warn("[RMQ name: {}] Failed to open RMQ channel", connectionName);
            closeConnection();
            return false;
        } else {
            log.debug("[RMQ name: {}] succeed to open RMQ connection", connectionName);
            return true;
        }
    }

    public void disconnect() {
        closeChannel();
        log.debug("[RMQ name: {}] succeed to close RMQ channel", connectionName);
        closeConnection();
        log.debug("[RMQ name: {}] succeed to close RMQ connection", connectionName);
    }

    /**
     * RabbitMQ broker 와 connection 을 맺기 위한 메서드
     */
    private boolean openConnection(String connectionName) {
        // 이미 채널이 열려있는 경우
        if (isConnected()) return true;
        ConnectionFactory factory = new ConnectionFactory();
        // 연결할 rabbitmq 서버 설정
        factory.setHost(this.host);
        factory.setUsername(this.userName);
        factory.setPassword(this.password);
        factory.setPort(this.port);

        // 기타 rabbitmq 옵션 설정
        UserConfig config = AppInstance.getInstance().getConfig();
        factory.setAutomaticRecoveryEnabled(config.isRmqAutoRecovery());
        factory.setNetworkRecoveryInterval(config.getRmqNetRecovery());
        factory.setRequestedHeartbeat(config.getRmqReqHb());
        factory.setConnectionTimeout(config.getRmqConnTimeout());

        factory.setExceptionHandler(new DefaultExceptionHandler() {
            @Override
            // 연결 드라이버 스레드가 처리할 수 없는 예외
            public void handleUnexpectedConnectionDriverException(Connection con, Throwable exception) {
                log.error("[RMQ name: {}] Queue Connect Fail", connectionName);
            }
            @Override
            // 연결 드라이버 스레드가 연결 복구 중 예외 발생
            public void handleConnectionRecoveryException(Connection conn, Throwable exception) {
                log.error("[RMQ name: {}] Rabbitmq connection recovery fail", connectionName);
            }
            @Override
            // 연결 드라이버 스레드가 채널 복구 중 예외 발생
            public void handleChannelRecoveryException(Channel ch, Throwable exception) {
                log.error("[RMQ name: {}] Rabbitmq channel recovery fail", connectionName);
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
            log.error("[RMQ name: {}] RmqTransport.makeConnection ", connectionName, e);
            return false;
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            log.error("RmqTransport.closeConnection ", e);
        }
    }

    /**
     * 채널을 생성하기 위한 메서드, 서버일 경우 구독할 큐를 선언한다
     */
    private boolean openChannel(boolean declareQueue) {
        try {
            // 이미 채널이 열려있는 경우
            if (isConnected()) return true;

            this.channel = this.connection.createChannel();
            // 채널 복구 정책
            ((Recoverable) channel).addRecoveryListener(new RecoveryListener() {
                @Override
                public void handleRecovery(Recoverable recoverable) {
                    if (recoverable instanceof Channel) {
                        log.error("[RMQ name: {}] recovery succeed. ", queueName);
                    }
                }
                @Override
                public void handleRecoveryStarted(Recoverable recoverable) {
                    log.error("[RMQ name: {}] recovery started. ", queueName);
                }
            });

            if (declareQueue) {
                channel.queueDeclare(queueName, false, false, false, null);
            }
            return true;
        } catch (Exception e) {
            log.error("RmqTransport.makeChannel ", e);
            return false;
        }
    }

    private void closeChannel() {
        try {
            channel.close();
        } catch (Exception e) {
            log.error("RmqTransport.closeChannel ", e);
        }
    }

    public boolean isConnected() {
        return (channel != null && channel.isOpen());
    }

    protected String getQueueName() {
        return queueName;
    }

    protected Channel getChannel() {
        return channel;
    }


}
