package moomoo.rmq.simulator.module.variable;

public class VariableInfo {
    private final String name;
    private final String type;
    private final int length;
    private final String format;

    public VariableInfo(String name, String type, int length, String format) {
        this.name = name;
        this.type = type;
        this.length = length;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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
                ", length=" + length +
                ", format='" + format + '\'' +
                '}';
    }
}
