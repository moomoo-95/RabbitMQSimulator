package moomoo.rmq.simulator.module.variable;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
            case "uuid":
                return UUID.randomUUID().toString();
            case "date":
                return new SimpleDateFormat(variableInfo.getFormat()).format(new Date(System.currentTimeMillis()));
//            case "string":
//                break;
//            case "int":
//                break;
//            case "long":
//                break;
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
