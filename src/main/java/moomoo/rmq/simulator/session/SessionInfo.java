package moomoo.rmq.simulator.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionInfo {
    private final String id;
    private final int commandSize;

    private Map<String, String> variableMap = new ConcurrentHashMap<>();

    private AtomicInteger commandIndex;

    private long wakeUpTime;

    public SessionInfo(String id, int commandSize) {
        this.id = id;
        this.commandSize = commandSize;
        this.commandIndex = new AtomicInteger(0);
        this.wakeUpTime = 0L;
    }

    public String putAndGetVariable(String key, String value) {
        variableMap.putIfAbsent(key, value);
        return variableMap.get(key);
    }

    public int getCommandIndex() {
        return commandIndex.get();
    }

    public void incrementCommandIndex() {
        commandIndex.incrementAndGet();
    }

    public int getAndIncrementCommandIndex() {
        return commandIndex.getAndIncrement();
    }

    public void pauseSession(long pauseTime) {
        wakeUpTime = System.currentTimeMillis() + pauseTime;
    }

    public boolean isAwake() {
        return wakeUpTime <= System.currentTimeMillis() && commandIndex.get() < commandSize;
    }

    public boolean isCompleted() {
        return commandIndex.get() >= commandSize;
    }


}
