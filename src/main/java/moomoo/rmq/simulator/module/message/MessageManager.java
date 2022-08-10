package moomoo.rmq.simulator.module.message;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageManager {

    private static final class Singleton { private static final MessageManager INSTANCE = new MessageManager(); }

    private Map<String, MessageInfo> messageMap = new ConcurrentHashMap<>();

    public MessageManager() {
        // noting
    }

    public static MessageManager getInstance() {
        return Singleton.INSTANCE;
    }

    public void setMessageMap(Map<String, MessageInfo> messageMap) {
        this.messageMap = messageMap;
    }

    public boolean isMessageKey(String key) {
        return messageMap.containsKey(key);
    }
}
