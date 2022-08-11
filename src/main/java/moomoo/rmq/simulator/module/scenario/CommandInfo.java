package moomoo.rmq.simulator.module.scenario;

import moomoo.rmq.simulator.module.base.ValueType;

import java.util.HashMap;
import java.util.Map;

/**
 * 개별 시나리오 내 개별 명령어에 대한 정보를 담은 객체
 */
public class CommandInfo {
    private final String type;

    private String name;
    private int pauseTime;

    private Map<String, String> valueMap = new HashMap<>();

    public CommandInfo(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }

    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, String> valueMap) {
        this.valueMap = valueMap;
    }

    @Override
    public String toString() {
        if(type.equalsIgnoreCase(ValueType.COMMAND_TYPE_PAUSE)) {
            return "[" + type + "] " + pauseTime + "ms\n";
        } else {
            return " [" + type + "] " + name + "\n - variables : " + valueMap.toString() + "\n";
        }
    }
}