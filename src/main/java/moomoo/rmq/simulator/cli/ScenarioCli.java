package moomoo.rmq.simulator.cli;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.module.scenario.ScenarioInfo;
import moomoo.rmq.simulator.module.scenario.ScenarioManager;
import moomoo.rmq.simulator.service.ServiceManager;
import moomoo.rmq.simulator.util.CommonUtil;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class ScenarioCli {

    private static final String LINE = "--------------------------------------\n";

    private static ScenarioManager scenarioManager = ScenarioManager.getInstance();

    private final List<String> scenarioIndexList;

    private ScenarioInfo selectedScenario = null;

    public ScenarioCli() {
        scenarioIndexList = scenarioManager.getScenarioInfoMap().keySet().stream().collect(Collectors.toList());
    }

    public void startCil() {
        int selectNumber = -1;

        while (selectNumber == -1) {
            selectNumber = selectScenarioPrint();
            if(selectNumber == -2) {
                ServiceManager.getInstance().stopService();
                return;
            }
        }

        selectedScenario = null;
    }

    /**
     * 시나리오 출력 및 진행할 시나리오를 입력받는 메서드
     * @return
     */
    private int selectScenarioPrint() {
        StringBuilder builder = new StringBuilder("\n");
        builder.append(LINE);
        for(int idx = 0; idx < scenarioIndexList.size(); idx++){
            builder.append((idx+1)+". "+scenarioIndexList.get(idx)+"\n");
        }
        builder.append("q. quit\n");
        builder.append(LINE);
        log.debug(builder.toString());

        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        if(input.equalsIgnoreCase("q")) { return -2; }
        int selectNumber = CommonUtil.parseInteger(input, 0) - 1;

        return confirmSelectScenario(selectNumber);
    }

    private int confirmSelectScenario(int selectNumber) {
        if (selectNumber < 0 || selectNumber >= scenarioManager.getScenarioSize()) return -1;
        String scenarioName = scenarioIndexList.get(selectNumber);
        ScenarioInfo scenarioInfo = scenarioManager.getScenarioInfo(scenarioName);
        // 이 부분에서 반환된다면 에러로 확인 필요
        if(scenarioInfo == null) {
            log.warn("ScenarioInfo [{}] is null", scenarioName);
            return -1;
        }

        StringBuilder builder = new StringBuilder("\n");
        builder.append(LINE);
        builder.append(scenarioInfo.getName()+"\n");
        builder.append(LINE);
        builder.append(scenarioInfo.toString()+"\n");
        builder.append(LINE);
        builder.append("Do you want to select this scenario?(y/n)\n");
        builder.append(LINE);
        log.debug(builder.toString());

        Scanner scanner = new Scanner(System.in);
        String answer = scanner.next();

        if (answer.equalsIgnoreCase("y")) {
            selectedScenario = scenarioInfo;
            return selectNumber;
        } else {
            return -1;
        }
    }
}
