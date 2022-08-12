package moomoo.rmq.simulator.session;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
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

    public String updateAndGetVariable(String key, String value) {
        if(variableMap.containsKey(key)) variableMap.replace(key, value);
        else variableMap.put(key, value);
        return variableMap.get(key);
    }

    public String getVariable(String key) {
        return variableMap.get(key);
    }

    public boolean isVariableExist(String key) {
        return variableMap.containsKey(key);
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
