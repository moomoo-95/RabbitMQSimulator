package moomoo.rmq.simulator.module.variable;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static moomoo.rmq.simulator.module.base.ValueType.*;
import static moomoo.rmq.simulator.util.VariableUtil.*;

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
    public String createVariableInfo (String name) {
        if(name.isEmpty()) {
            return "";
        }

        VariableInfo variableInfo = variableMap.get(name);
        if (variableInfo == null) {
            log.debug("variable name [{}] do not exist.", name);
            return "";
        }

        switch (variableInfo.getType().toLowerCase()) {
            case VARIABLE_TYPE_UUID:
                return createUUID();
            case VARIABLE_TYPE_DATE:
                return createCurrentDate(variableInfo.getFormat());
            case VARIABLE_TYPE_STRING:
                return createRandomString(variableInfo.getLength());
            case VARIABLE_TYPE_INT:
                return createRandomInt(variableInfo.getLength());
            default:
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
