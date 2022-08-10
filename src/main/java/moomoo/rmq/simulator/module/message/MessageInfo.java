package moomoo.rmq.simulator.module.message;

import java.util.List;

public class MessageInfo {
    private final String fileName;

    private List<Integer> variableIndex;
    private List<String> message;

    public MessageInfo(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public List<Integer> getVariableIndex() {
        return variableIndex;
    }

    public void setVariableIndex(List<Integer> variableIndex) {
        this.variableIndex = variableIndex;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "fileName='" + fileName + '\'' +
                ", variableIndex=" + variableIndex +
                ", message=" + message +
                '}';
    }
}
