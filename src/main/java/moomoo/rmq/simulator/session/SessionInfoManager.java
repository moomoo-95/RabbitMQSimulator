package moomoo.rmq.simulator.session;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.scenario.CommandInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class SessionInfoManager {

    private static final class Singleton { private static final SessionInfoManager INSTANCE = new SessionInfoManager(); }

    private Set<SessionInfo> sessionSet = new HashSet<>();
    private List<CommandInfo> commandList;

    public SessionInfoManager() {
        // noting
    }

    public static SessionInfoManager getInstance() {
        return Singleton.INSTANCE;
    }

    public void createSession(int count, String id, List<CommandInfo> commandList) {
        this.commandList = new ArrayList<>(commandList);
        for (int idx = 0; idx < count; idx++) {
            sessionSet.add(new SessionInfo(id, commandList.size()));
        }
    }

    public void clear() {
        sessionSet.clear();
        commandList.clear();
    }

    public boolean isAllCompleteSession() {
        return sessionSet.stream().filter(SessionInfo::isCompleted).count() == sessionSet.size();
    }

    public Set<SessionInfo> getSessionSet() {
        return sessionSet;
    }

    public CommandInfo getCommandInfo(int index) {
        return commandList.get(index);
    }
}
