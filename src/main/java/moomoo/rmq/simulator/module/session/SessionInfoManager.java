package moomoo.rmq.simulator.module.session;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.module.scenario.CommandInfo;

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
        this.commandList = commandList;
        for (int idx = 0; idx < count; idx++) {
            sessionSet.add(new SessionInfo(id));
        }
    }

}
