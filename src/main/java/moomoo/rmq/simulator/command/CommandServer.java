package moomoo.rmq.simulator.command;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.RmqManager;
import moomoo.rmq.simulator.message.MessageInfo;
import moomoo.rmq.simulator.message.MessageManager;
import moomoo.rmq.simulator.scenario.CommandInfo;
import moomoo.rmq.simulator.scenario.ScenarioInfo;
import moomoo.rmq.simulator.scenario.ScenarioManager;
import moomoo.rmq.simulator.service.ServiceManager;
import moomoo.rmq.simulator.session.SessionInfo;
import moomoo.rmq.simulator.session.SessionInfoManager;
import moomoo.rmq.simulator.util.CommonUtil;
import moomoo.rmq.simulator.variable.VariableFactory;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static moomoo.rmq.simulator.command.handler.CommandHandler.printScenarioCommandFlow;
import static moomoo.rmq.simulator.command.handler.CommandHandler.printScenarioList;
import static moomoo.rmq.simulator.util.ParsingType.COMMAND_TYPE_PAUSE;
import static moomoo.rmq.simulator.util.ParsingType.COMMAND_TYPE_SEND;

@Slf4j
public class CommandServer implements Runnable {

    private static ScenarioManager scenarioManager = ScenarioManager.getInstance();
    private static MessageManager messageManager = MessageManager.getInstance();
    private static VariableFactory variableFactory = VariableFactory.getInstance();
    private static SessionInfoManager sessionInfoManager = SessionInfoManager.getInstance();
    private static RmqManager rmqManager = RmqManager.getInstance();

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
     * ???????????? ??????????????? ???????????? ?????????
     */
    private void selectScenario() {
        printScenarioList(scenarioIndexList);

        String input = scanner.next();
        if(input.equalsIgnoreCase("q")) { stop(); }
        int selectNumber = CommonUtil.parseInteger(input, 0) - 1;

        confirmSelectScenario(selectNumber);
    }

    /**
     * ???????????? ??????????????? ??? ??? ??? ???????????? ?????????
     */
    private void confirmSelectScenario(int selectNumber) {
        if (selectNumber < 0 || selectNumber >= scenarioManager.getScenarioSize()) return;
        String scenarioName = scenarioIndexList.get(selectNumber);
        ScenarioInfo scenarioInfo = scenarioManager.getScenarioInfo(scenarioName);
        // ??? ???????????? ??????????????? ????????? ?????? ??????
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
                // ?????? ??? ????????? ????????? ????????? ????????? ???????????? ?????? ??????
                sessionSet.stream().filter(SessionInfo::isAwake).forEach( sessionInfo -> {
                    CommandInfo commandInfo = sessionInfoManager.getCommandInfo(sessionInfo.getCommandIndex());
                    // send ?????? ??? ????????? ?????? ??? ??????
                    if(commandInfo.isType(COMMAND_TYPE_SEND)) {
                        MessageInfo messageInfo = messageManager.getMessageInfo(commandInfo.getName());
                        List<String> originMsg = messageInfo.getMessage();
                        // ???????????? ?????? ??????
                        messageInfo.getVariableIndex().forEach( i -> {
                            String key = originMsg.get(i);
                            String variable = variableFactory.createVariable(sessionInfo, commandInfo.getValueMap(), key);
                            originMsg.set(i, variable);
                        });
                        // ????????? ????????? ???????????? ???????????? ??????
                        StringBuilder builder = new StringBuilder();
                        originMsg.forEach(builder::append);
                        // ????????? queue ??? ??????
                        log.debug("send : {}", builder);
                        rmqManager.getRmqClient().send(builder.toString());
                        sessionInfo.incrementCommandIndex();
                    }
                    // pause ????????? ?????? ??????
                    else if(commandInfo.isType(COMMAND_TYPE_PAUSE)) {
                        log.debug("{} sleep {}", commandInfo.getType(), commandInfo.getPauseTime());
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
