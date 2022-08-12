package moomoo.rmq.simulator.variable;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.session.SessionInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static moomoo.rmq.simulator.util.ParsingType.*;
import static moomoo.rmq.simulator.util.VariableUtil.*;

/**
 * variable.xml 에서 읽어온 변수 객체를 토대로 생성하는 클래스
 */
@Slf4j
public class VariableFactory {

    private static final class Singleton { private static final VariableFactory INSTANCE = new VariableFactory(); }

    private Map<String, VariableInfo> variableMap = new ConcurrentHashMap<>();

    public VariableFactory() {
        // noting
    }

    public static VariableFactory getInstance() {
        return Singleton.INSTANCE;
    }

    // 해당 변수에 대한 값을 설정하는 메서드
    public String createVariable(SessionInfo sessionInfo, Map<String, String> scenarioValueMap, String name) {
        if(name.isEmpty()) {
            log.warn("Do not create variable. (name is null)");
            return "";
        }

        VariableInfo variableInfo = variableMap.get(name);
        // xml 파싱 시 일치하지 않으면 생성되지 않도록 설정, 이 부분에서 반환된다면 에러로 확인 필요
        if (variableInfo == null) {
            log.debug("variable name [{}] do not exist.", name);
            return "";
        }

        // 시나리오에 지정되어 있다면 그 값 사용
        if(scenarioValueMap.containsKey(name)) {
            return sessionInfo.updateAndGetVariable(name, scenarioValueMap.get(name));
        }

        // 변수가 고정이고 이미 저장되어 있다면 기존의 변수로 반환
        if(variableInfo.isFixed() && sessionInfo.isVariableExist(name)) {
            return sessionInfo.getVariable(name);
        }

        switch (variableInfo.getType().toLowerCase()) {
            case VARIABLE_TYPE_UUID:
                return sessionInfo.updateAndGetVariable(name, createUUID());
            case VARIABLE_TYPE_DATE:
                return sessionInfo.updateAndGetVariable(name, createCurrentDate(variableInfo.getFormat()));
            case VARIABLE_TYPE_STRING:
                return sessionInfo.updateAndGetVariable(name, createRandomString(variableInfo.getLength()));
            case VARIABLE_TYPE_INT:
                return sessionInfo.updateAndGetVariable(name, createRandomInt(variableInfo.getLength()));
            default:
                // xml 파싱 시 정의된 variable name 이 아니라면 생성되지 않도록 설정, 이 부분에서 반환된다면 에러로 확인 필요
                log.debug("variable type [{}] is not a supported type.", variableInfo.getType());
                return "";
        }
    }

    public void setVariableMap(Map<String, VariableInfo> variableMap) {
        this.variableMap = variableMap;
    }

    public boolean isVariableKey(String key) {
        return variableMap.containsKey(key);
    }
}
