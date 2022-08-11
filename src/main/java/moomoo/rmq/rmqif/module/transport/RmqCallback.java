package moomoo.rmq.rmqif.module.transport;

import java.util.Date;

public interface RmqCallback {
    void onReceived(String msg, Date ts);
}
