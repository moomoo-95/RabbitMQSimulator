package moomoo.rmq.simulator.command;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.RmqManager;
import moomoo.rmq.simulator.command.handler.CommandHandler;
import moomoo.rmq.simulator.message.MessageInfo;
import moomoo.rmq.simulator.message.MessageManager;
import moomoo.rmq.simulator.scenario.CommandInfo;
import moomoo.rmq.simulator.scenario.ScenarioInfo;
import moomoo.rmq.simulator.scenario.ScenarioManager;
import moomoo.rmq.simulator.session.SessionInfo;
import moomoo.rmq.simulator.session.SessionInfoManager;
import moomoo.rmq.simulator.service.ServiceManager;
import moomoo.rmq.simulator.util.CommonUtil;
import moomoo.rmq.simulator.variable.VariableFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static moomoo.rmq.simulator.command.handler.CommandHandler.printScenarioCommandFlow;
import static moomoo.rmq.simulator.command.handler.CommandHandler.printScenarioList;
import static moomoo.rmq.simulator.util.ParsingType.*;

@Slf4j
public class CommandServer implements Runnable {

    private static ScenarioManager scenarioManager = ScenarioManager.getInstance();
    private static MessageManager messageManager = MessageManager.getInstance();
    private static VariableFactory variableFactory = VariableFactory.getInstance();
    private static SessionInfoManager sessionInfoManager = SessionInfoManager.getInstance();

    private final Scanner scanner;
    private final List<String> scenarioIndexList;

    private boolean isQuit = false;
    private boolean startScenario = false;

    public CommandServer() {
        scanner = new Scanner(System.in);
        scenarioIndexList = scenarioManager.getScenarioInfoMap().keySet().stream().collect(Collectors.toList());
    }

    @Override
    public void run() {
        while (!isQuit) {
            selectScenario();
        }
        ServiceManager.getInstance().stopService();
    }

    public void stop() {
        isQuit = true;
        scanner.close();
    }

    /**
     * 실행시킬 시나리오를 입력받는 메서드
     */
    private void selectScenario() {
        printScenarioList(scenarioIndexList);

        String input = scanner.next();
        if(input.equalsIgnoreCase("q")) { stop(); }
        int selectNumber = CommonUtil.parseInteger(input, 0) - 1;

        confirmSelectScenario(selectNumber);
    }

    /**
     * 입력받은 시나리오를 한 번 더 확인하는 메서드
     */
    private void confirmSelectScenario(int selectNumber) {
        if (selectNumber < 0 || selectNumber >= scenarioManager.getScenarioSize()) return;
        String scenarioName = scenarioIndexList.get(selectNumber);
        ScenarioInfo scenarioInfo = scenarioManager.getScenarioInfo(scenarioName);
        // 이 부분에서 반환된다면 에러로 확인 필요
        if(scenarioInfo == null) {
            log.warn("ScenarioInfo [{}] is null", scenarioName);
            return;
        }

        printScenarioCommandFlow(scenarioInfo);
        String answer = scanner.next();

        if (answer.equalsIgnoreCase("y")) {
            scenarioProcessing(scenarioInfo);
        }
    }

    private void scenarioProcessing(ScenarioInfo scenarioInfo) {
        sessionInfoManager.createSession(scenarioInfo.getCount(), scenarioInfo.getId(), scenarioInfo.getCommandInfoList());
        startScenario = true;
        log.debug("Scenario [{}] is start.", scenarioInfo.getName());
        CommandConsumer commandConsumer = new CommandConsumer();
        commandConsumer.run();
    }

    private class CommandConsumer implements Runnable {

        @Override
        public void run() {
            processCommand();
        }

        private void processCommand() {
            while (startScenario){
                CommonUtil.trySleep(20);

                Set<SessionInfo> sessionSet = sessionInfoManager.getSessionSet();
                sessionSet.stream().filter(SessionInfo::isAwake).forEach( sessionInfo -> {
                    CommandInfo commandInfo = sessionInfoManager.getCommandInfo(sessionInfo.getCommandIndex());
                    // send 명령 시 메시지 생성 및 전송
                    if(commandInfo.isType(COMMAND_TYPE_SEND)) {
                        MessageInfo messageInfo = messageManager.getMessageInfo(commandInfo.getName());
                        List<String> originMsg = messageInfo.getMessage();
                        // 메시지 생성, 기존의 있는 변수는 그대로 사용, 없다면 새로 생성
                        messageInfo.getVariableIndex().forEach( i -> {
                            String key = originMsg.get(i);
                            String variable = sessionInfo.putAndGetVariable(key, variableFactory.createVariableInfo(key));
                            originMsg.set(i, variable);
                        });
                        StringBuilder builder = new StringBuilder();
                        originMsg.forEach(s -> builder.append(s));
                        log.debug("send : {}", builder.toString());
//                        RmqManager.getInstance().getRmqClient().send(builder.toString());
                        sessionInfo.incrementCommandIndex();
                    }
                    // pause 명령시 시간 설정
                    else if(commandInfo.isType(COMMAND_TYPE_PAUSE)) {
                        log.debug("{} sleep {}", commandInfo.getName(), commandInfo.getPauseTime());
                        sessionInfo.pauseSession(commandInfo.getPauseTime());
                        sessionInfo.incrementCommandIndex();
                    } else {
                        log.debug("{} recv expected {}", commandInfo.getName(), commandInfo);
                        sessionInfo.incrementCommandIndex();
                    }
                });

                if(sessionInfoManager.isAllCompleteSession()) {
                    sessionInfoManager.clear();
                    startScenario = false;
                }
            }
        }
    }
}
