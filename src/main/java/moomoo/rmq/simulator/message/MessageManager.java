package moomoo.rmq.simulator.message;

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

    public MessageInfo getMessageInfo(String messageName) {
        return messageMap.get(messageName);
    }

    public void setMessageMap(Map<String, MessageInfo> messageMap) {
        this.messageMap = messageMap;
    }

    public boolean isMessageKey(String key) {
        return messageMap.containsKey(key);
    }
}
