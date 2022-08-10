package moomoo.rmq.simulator.module.scenario;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ScenarioManager {

    private static final class Singleton { private static final ScenarioManager INSTANCE = new ScenarioManager(); }

    private Map<String, ScenarioInfo> scenarioInfoMap = new ConcurrentHashMap<>();

    public ScenarioManager() {
        // noting
    }

    public static ScenarioManager getInstance() {
        return Singleton.INSTANCE;
    }

    public Map<String, ScenarioInfo> getScenarioInfoMap() {
        return scenarioInfoMap;
    }

    public void setScenarioMap(Map<String, ScenarioInfo> scenarioInfoMap) {
        this.scenarioInfoMap = scenarioInfoMap;
    }

    public ScenarioInfo getScenarioInfo(String name) {
        if (name == null) {
            log.warn("name is null.");
        }
        return this.scenarioInfoMap.get(name);
    }

    public int getScenarioSize() {
        return this.scenarioInfoMap.size();
    }

}
