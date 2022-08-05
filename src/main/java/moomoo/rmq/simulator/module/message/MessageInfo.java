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
        StringBuilder stringBuilder = new StringBuilder("MessageInfo{" +
                "fileName='" + fileName + '\'' +
                ", variableIndex=" + variableIndex +
                ", message=\n");
        for (int idx = 0; idx < message.size(); idx++) {
            stringBuilder.append(idx + " : " + message.get(idx) + "\n");
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }
}
