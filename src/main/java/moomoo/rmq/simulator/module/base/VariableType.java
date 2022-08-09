package moomoo.rmq.simulator.module.base;

import java.util.HashSet;
import java.util.Set;

public class VariableType {
    public static final String VARIABLE_TYPE_UUID = "uuid";
    public static final String VARIABLE_TYPE_DATE = "date";
    public static final String VARIABLE_TYPE_STRING = "string";
    public static final String VARIABLE_TYPE_INT = "int";

    public static final Set<String> VARIABLE_TYPE_SET = new HashSet<>();

    static {
        VARIABLE_TYPE_SET.add(VARIABLE_TYPE_UUID);
        VARIABLE_TYPE_SET.add(VARIABLE_TYPE_DATE);
        VARIABLE_TYPE_SET.add(VARIABLE_TYPE_STRING);
        VARIABLE_TYPE_SET.add(VARIABLE_TYPE_INT);
    }

    public static boolean isVariableType(String type) {
        return VARIABLE_TYPE_SET.contains(type.toLowerCase());
    }
}
