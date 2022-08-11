package moomoo.rmq.simulator.module.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionInfo {
    private final String id;

    private Map<String, String> variableMap = new ConcurrentHashMap<>();

    private AtomicInteger commandIndex;
    private long wakeUpTime;

    public SessionInfo(String id) {
        this.id = id;
        this.commandIndex = new AtomicInteger(0);
        this.wakeUpTime = 0L;
    }

    public int getCommandIndex() {
        return commandIndex.get();
    }

    public int getAndIncrementCommandIndex() {
        return commandIndex.getAndIncrement();
    }

    public void pauseSession(long pauseTime) {
        wakeUpTime = System.currentTimeMillis() + pauseTime;
    }

    public boolean isAwake() {
        return wakeUpTime <= System.currentTimeMillis();
    }


}
