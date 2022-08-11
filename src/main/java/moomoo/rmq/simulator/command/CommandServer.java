package moomoo.rmq.simulator.command;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.module.scenario.ScenarioInfo;
import moomoo.rmq.simulator.module.scenario.ScenarioManager;
import moomoo.rmq.simulator.service.ServiceManager;
import moomoo.rmq.simulator.util.CommonUtil;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class CommandServer implements Runnable {

    private static final String LINE = "-------------------------------------------------\n";

    private static ScenarioManager scenarioManager = ScenarioManager.getInstance();

    private final Scanner scanner;
    private final List<String> scenarioIndexList;

    private ScenarioInfo selectedScenario = null;

    private boolean isQuit = false;

    public CommandServer() {
        scanner = new Scanner(System.in);
        scenarioIndexList = scenarioManager.getScenarioInfoMap().keySet().stream().collect(Collectors.toList());
    }

    @Override
    public void run() {

        while (!isQuit) {
            isQuit = commandLoop();
        }
        ServiceManager.getInstance().stopService();
    }

    public void stop() {
        isQuit = true;
        scanner.close();
    }

    /**
     * q 입력 또는 프로세스 종료가 되기 전까지 입력을 받기 위한 스레드
     * @return
     */
    private boolean commandLoop() {
        int selectNumber = -1;

        while (selectNumber == -1) {
            selectNumber = selectScenario();
            if(isQuit || selectNumber == -2) return true;
        }
        // todo 선택된 시나리오 처리 부분

        selectedScenario = null;
        return false;
    }

    /**
     * 실행시킬 시나리오를 입력받는 메서드
     */
    private int selectScenario() {
        printScenarioList();

        String input = scanner.next();
        if(input.equalsIgnoreCase("q")) { return -2; }
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
            selectedScenario = scenarioInfo;
            return selectNumber;
        } else {
            return -1;
        }
    }

    /**
     * 시나리오 목록 출력
     */
    private void printScenarioList() {
        StringBuilder builder = new StringBuilder("\n");
        builder.append(LINE);
        for(int idx = 0; idx < scenarioIndexList.size(); idx++){
            builder.append((idx+1)+". "+scenarioIndexList.get(idx)+"\n");
        }
        builder.append("q. quit\n");
        builder.append(LINE);
        log.debug(builder.toString());
    }

    /**
     * 특정 시나리오의 상세 명령 흐름 출력
     * @param scenarioInfo
     */
    private void printScenarioCommandFlow(ScenarioInfo scenarioInfo) {
        StringBuilder builder = new StringBuilder("\n");
        builder.append(LINE);
        builder.append("["+scenarioInfo.getName()+"] [cnt : "+scenarioInfo.getCount()+"] [id : "+scenarioInfo.getId()+"] \n");
        builder.append(LINE);
        scenarioInfo.getCommandInfoList().forEach( c -> builder.append(c.toString()));
        builder.append(LINE);
        builder.append("Do you want to select this scenario?(y/n)\n");
        builder.append(LINE);
        log.debug(builder.toString());
    }
}
