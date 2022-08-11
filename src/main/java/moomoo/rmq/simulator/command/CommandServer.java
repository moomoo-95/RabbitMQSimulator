package moomoo.rmq.simulator.command;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.module.scenario.CommandInfo;
import moomoo.rmq.simulator.module.scenario.ScenarioInfo;
import moomoo.rmq.simulator.module.scenario.ScenarioManager;
import moomoo.rmq.simulator.module.session.SessionInfoManager;
import moomoo.rmq.simulator.service.ServiceManager;
import moomoo.rmq.simulator.util.CommonUtil;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static moomoo.rmq.simulator.command.CommandHandler.printScenarioCommandFlow;
import static moomoo.rmq.simulator.command.CommandHandler.printScenarioList;
import static moomoo.rmq.simulator.module.base.ValueType.*;

@Slf4j
public class CommandServer implements Runnable {

    private static ScenarioManager scenarioManager = ScenarioManager.getInstance();

    private final Scanner scanner;
    private final List<String> scenarioIndexList;

    private boolean isQuit = false;

    public CommandServer() {
        scanner = new Scanner(System.in);
        scenarioIndexList = scenarioManager.getScenarioInfoMap().keySet().stream().collect(Collectors.toList());
    }

    @Override
    public void run() {
        commandLoop();
    }

    public void stop() {
        isQuit = true;
        scanner.close();
    }

    /**
     * q 입력 또는 프로세스 종료가 되기 전까지 입력을 받기 위한 스레드
     * @return
     */
    private void commandLoop() {
        int selectNumber = -1;

        while (!isQuit && selectNumber == -1) {
            selectNumber = selectScenario();
        }
        ServiceManager.getInstance().stopService();
    }

    /**
     * 실행시킬 시나리오를 입력받는 메서드
     */
    private int selectScenario() {
        printScenarioList(scenarioIndexList);

        String input = scanner.next();
        if(input.equalsIgnoreCase("q")) { stop(); }
        int selectNumber = CommonUtil.parseInteger(input, 0) - 1;

        return confirmSelectScenario(selectNumber);
    }

    /**
     * 입력받은 시나리오를 한 번 더 확인하는 메서드
     */
    private int confirmSelectScenario(int selectNumber) {
        if (selectNumber < 0 || selectNumber >= scenarioManager.getScenarioSize()) return -1;
        String scenarioName = scenarioIndexList.get(selectNumber);
        ScenarioInfo scenarioInfo = scenarioManager.getScenarioInfo(scenarioName);
        // 이 부분에서 반환된다면 에러로 확인 필요
        if(scenarioInfo == null) {
            log.warn("ScenarioInfo [{}] is null", scenarioName);
            return -1;
        }

        printScenarioCommandFlow(scenarioInfo);
        String answer = scanner.next();

        if (answer.equalsIgnoreCase("y")) {

            scenarioProcessing(scenarioInfo);
            return selectNumber;
        } else {
            return -1;
        }
    }

    private void scenarioProcessing(ScenarioInfo scenarioInfo) {
        SessionInfoManager.getInstance().createSession(scenarioInfo.getCount(), scenarioInfo.getId(), scenarioInfo.getCommandInfoList());

//        scenarioInfo.getCommandInfoList().forEach(this::commandProcessing);
    }

    private boolean commandProcessing(CommandInfo commandInfo) {
        boolean result = false;
        switch (commandInfo.getType()) {
            case COMMAND_TYPE_PAUSE:
                result = CommandHandler.processPauseCommand(commandInfo.getPauseTime());
                break;
            case COMMAND_TYPE_SEND:
                break;
            case COMMAND_TYPE_RECV:
                break;
            default:
        }
        return result;
    }
}
