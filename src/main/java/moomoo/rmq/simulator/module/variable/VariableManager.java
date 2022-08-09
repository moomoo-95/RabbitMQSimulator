package moomoo.rmq.simulator.module.variable;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

import static moomoo.rmq.simulator.module.base.VariableType.*;
import static moomoo.rmq.simulator.util.VariableUtil.*;

@Slf4j
public class VariableManager {

    private static final class Singleton { private static final VariableManager INSTANCE = new VariableManager(); }

    private ConcurrentHashMap<String, VariableInfo> variableMap = new ConcurrentHashMap<>();

    public VariableManager() {
        // noting
    }

    public static VariableManager getInstance() {
        return Singleton.INSTANCE;
    }

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

    public ConcurrentHashMap<String, VariableInfo> getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(ConcurrentHashMap<String, VariableInfo> variableMap) {
        this.variableMap = variableMap;
    }

    public boolean isVariableKey(String key) {
        return variableMap.containsKey(key);
    }
}
