package moomoo.rmq.simulator.util;

import java.util.HashSet;
import java.util.Set;

public class ParsingType {
    public static final String VARIABLE_TYPE_UUID = "uuid";
    public static final String VARIABLE_TYPE_DATE = "date";
    public static final String VARIABLE_TYPE_STRING = "string";
    public static final String VARIABLE_TYPE_INT = "int";

    public static final String COMMAND_TYPE_PAUSE = "pause";
    public static final String COMMAND_TYPE_SEND = "send";
    public static final String COMMAND_TYPE_RECV = "recv";

    protected static final Set<String> VARIABLE_TYPE_SET = new HashSet<>();
    protected static final Set<String> COMMAND_TYPE_SET = new HashSet<>();

    static {
        VARIABLE_TYPE_SET.add(VARIABLE_TYPE_UUID);
        VARIABLE_TYPE_SET.add(VARIABLE_TYPE_DATE);
        VARIABLE_TYPE_SET.add(VARIABLE_TYPE_STRING);
        VARIABLE_TYPE_SET.add(VARIABLE_TYPE_INT);

        COMMAND_TYPE_SET.add(COMMAND_TYPE_PAUSE);
        COMMAND_TYPE_SET.add(COMMAND_TYPE_SEND);
        COMMAND_TYPE_SET.add(COMMAND_TYPE_RECV);
    }

    private ParsingType() {
        // nothing
    }

    public static boolean isVariableType(String type) {
        return VARIABLE_TYPE_SET.contains(type.toLowerCase());
    }

    public static boolean isCommandType(String type) {
        return COMMAND_TYPE_SET.contains(type.toLowerCase());
    }
}
