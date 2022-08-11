package moomoo.rmq.simulator.command;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.module.scenario.ScenarioInfo;

import java.util.List;

@Slf4j
public class CommandHandler {

    private static final String LINE = "-------------------------------------------------\n";

    private CommandHandler(){
        //nothing
    }

    /**
     * 시나리오 목록 출력
     */
    public static void printScenarioList(List<String> scenarioIndexList) {
        StringBuilder builder = new StringBuilder("\n");
        builder.append(LINE);
        for(int idx = 0; idx < scenarioIndexList.size(); idx++){
            builder.append((idx+1)+". "+scenarioIndexList.get(idx)+"\n");
        }
        builder.append("q. quit\n");
        builder.append(LINE);
        printConsole(builder.toString());
    }

    /**
     * 특정 시나리오의 상세 명령 흐름 출력
     * @param scenarioInfo
     */
    public static void printScenarioCommandFlow(ScenarioInfo scenarioInfo) {
        StringBuilder builder = new StringBuilder("\n");
        builder.append(LINE);
        builder.append("["+scenarioInfo.getName()+"] [cnt : "+scenarioInfo.getCount()+"] [id : "+scenarioInfo.getId()+"] \n");
        builder.append(LINE);
        scenarioInfo.getCommandInfoList().forEach( c -> builder.append(c.toString()));
        builder.append(LINE);
        builder.append("Do you want to select this scenario?(y/n)\n");
        builder.append(LINE);
        printConsole(builder.toString());
    }

    /**
     * pause [time] 명령어에 대한 처리, time 만큼 명령어 처리 멈춤
     */
    public static boolean processPauseCommand(int pauseTime) {
        long time = pauseTime;
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            log.error("CommandServer.processPauseCommand ", e);
            if (e.getClass() == InterruptedException.class) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
        return true;
    }

    public static void printConsole(String str) {
        System.out.println(str);
    }


}
