package moomoo.rmq.simulator.module.scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * 시나리오에 대한 정보를 담은 객체
 */
public class ScenarioInfo {
    private final String name;
    private final int count;
    private final String id;

    private List<CommandInfo> commandInfoList = new ArrayList<>();

    public ScenarioInfo(String name, int count, String id) {
        this.name = name;
        this.count = count;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    public List<CommandInfo> getCommandInfoList() {
        return commandInfoList;
    }

    public void setCommandInfoList(List<CommandInfo> commandInfoList) {
        this.commandInfoList = commandInfoList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ScenarioInfo{name='" + name + "', count=" + count + ", id='" + id + "'\ncommandInfoList=\n");
        commandInfoList.forEach( c -> builder.append(c.toString()));
        return builder.toString();
    }
}