package moomoo.rmq.simulator.util;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.config.UserConfig;
import moomoo.rmq.simulator.module.message.MessageInfo;
import moomoo.rmq.simulator.module.message.MessageManager;
import moomoo.rmq.simulator.module.variable.VariableFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class XmlParser
 * @brief 메인 시나리오 파일을 파싱하는 xml 파서
 * @author hyeon seong lim
 */
@Slf4j
public class MsgParser {

    private static final String MSG_FILE_EXTENSION = ".msg";

    private static UserConfig config = AppInstance.getInstance().getConfig();
    private static VariableFactory variableFactory = VariableFactory.getInstance();

    private MsgParser() {
        // Do nothing
    }

    /**
     * 메시지 포맷을 읽어 info 로 저장하는 메서드
     * @return
     */
    public static boolean readMsgDir() {
        String dirPath = config.getCommonMsgPath();
        if (!dirPath.endsWith(File.separator)) dirPath += File.separator;
        log.debug("msg dir path : {}", dirPath);

        File msgDir = new File(dirPath);
        if(!msgDir.isDirectory()) {
            log.warn("msg dir path [{}] do not exist.", dirPath);
            return false;
        }

        ConcurrentHashMap<String, MessageInfo> messageMap = new ConcurrentHashMap<>();
        String[] msgFileList = msgDir.list();
        // 각 파일 별 read
        for (String msgFileName : msgFileList) {
            File msgFile = new File(dirPath, msgFileName);
            log.debug("{} : {}", msgFile.getPath(), msgFile.isFile());
            // *.msg 파일만 read
            if (!msgFile.isFile() || !msgFileName.endsWith(MSG_FILE_EXTENSION)) continue;
            // 확장자를 제외한 파일 명이 messageInfo 의 key
            String fileName = msgFileName.substring(0, msgFileName.length() - MSG_FILE_EXTENSION.length());
            MessageInfo messageInfo = new MessageInfo(fileName);
            readMsgFile(messageInfo, msgFile);
            if(messageMap.putIfAbsent(fileName, messageInfo) != null) {
                log.warn("variable name [{}] is already exist", fileName);
            }
        }
        MessageManager.getInstance().setMessageMap(messageMap);

        messageMap.forEach( (k, v) -> log.debug("Message [{}] : {}", k, v.toString()) );
        log.debug("{} Message parsing... (OK)", config.getCommonScenarioFile());
        return true;
    }

    /**
     * 메시지 내용 중 변수부분을 구분하는 메서드
     * @param msgFile
     * @return
     */
    private static void readMsgFile(MessageInfo messageInfo, File msgFile) {
        List<String> message = new ArrayList<>();
        List<Integer> variableIndex = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(msgFile)) {
            StringBuilder stringBuilder = new StringBuilder(new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8));
            // 변수의 시작, 끝 인덱스 찾기
            List<Integer> indexPairList = findVariableIndex(stringBuilder.toString());
            int addIndex = 0;
            for (int idx = 0; idx < indexPairList.size(); idx = idx + 2) {
                int startIdx = indexPairList.get(idx);
                int endIdx = indexPairList.get(idx+1);
                String variableName = stringBuilder.substring(startIdx + 1, endIdx);
                //
                if (variableFactory.isVariableKey(variableName)) {
                    // 데이터 처리 편의성을 위해 변수 기준으로 string 을 분할하여 list 에 저장
                    if (addIndex < startIdx) message.add(stringBuilder.substring(addIndex, startIdx));
                    message.add(variableName);
                    variableIndex.add(message.size()-1);
                    addIndex = endIdx + 1;
                }
            }
            message.add(stringBuilder.substring(addIndex));

            messageInfo.setMessage(message);
            messageInfo.setVariableIndex(variableIndex);
        } catch (IOException e) {
            log.error("MsgParser.readMsgFile ", e);
            messageInfo.setMessage(new ArrayList<>());
            messageInfo.setVariableIndex(new ArrayList<>());
        }
    }

    /**
     * 변수를 찾아 시작과 끝 인덱스 리스트를 반환하는 메서드
     */
    private static List<Integer> findVariableIndex(String str) {
        List<Integer> variableIndex = new ArrayList<>();
        List<Integer> bracketStack = new ArrayList<>();

        for (int idx = 0; idx < str.length(); idx++) {
            if (str.charAt(idx) == '[') {
                bracketStack.add(idx);
            } else if (str.charAt(idx) == ']' && !bracketStack.isEmpty()) {
                variableIndex.add(bracketStack.remove(bracketStack.size()-1));
                variableIndex.add(idx);
            }
        }
        return variableIndex;
    }
}
