package moomoo.rmq.simulator.variable;

public class VariableInfo {
    private final String name;
    private final String type;
    private final boolean fixed;
    private final int length;
    private final String format;

    public VariableInfo(String name, String type, boolean fixed, int length, String format) {
        this.name = name;
        this.type = type;
        this.fixed = fixed;
        this.length = length;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isFixed() {
        return fixed;
    }

    public int getLength() {
        return length;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return "VariableInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", fixed=" + fixed +
                ", length=" + length +
                ", format='" + format + '\'' +
                '}';
    }
}
