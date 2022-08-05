package moomoo.rmq.rmqif.module.transport.base;

import java.util.Date;

public interface RmqCallback {
    void onReceived(String msg, Date ts);
}
