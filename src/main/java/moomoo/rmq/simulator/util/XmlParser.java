package moomoo.rmq.simulator.util;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.config.UserConfig;
import moomoo.rmq.simulator.module.message.MessageManager;
import moomoo.rmq.simulator.module.scenario.CommandInfo;
import moomoo.rmq.simulator.module.scenario.ScenarioInfo;
import moomoo.rmq.simulator.module.scenario.ScenarioManager;
import moomoo.rmq.simulator.module.variable.VariableFactory;
import moomoo.rmq.simulator.module.variable.VariableInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static moomoo.rmq.simulator.module.base.ValueType.*;
import static moomoo.rmq.simulator.util.CommonUtil.parseInteger;

/**
 * @class XmlParser
 * @brief 변수를 정의하는 variable.xml , 시나리오를 정의하는 scenario.xml 파일을 파싱하는 클래스
 * @author hyeon seong lim
 */
@Slf4j
public class XmlParser {

    // variable.xml
    private static final String VARIABLES_TAG = "variables";
    private static final String VARIABLE_TAG = "variable";

    private static final String NAME_ATTRIBUTE = "name";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String LENGTH_ATTRIBUTE = "length";
    private static final String FORMAT_ATTRIBUTE = "format";

    // scenario.xml
    private static final String SCENARIOS_TAG = "scenarios";
    private static final String SCENARIO_TAG = "scenario";
    private static final String COMMAND_TAG = "command";

    private static final String COUNT_ATTRIBUTE = "count";
    private static final String ID_ATTRIBUTE = "id";
    private static final String MS_ATTRIBUTE = "ms";
    private static final String VALUE_ATTRIBUTE = "value";

    private static UserConfig config = AppInstance.getInstance().getConfig();
    private static VariableFactory variableFactory = VariableFactory.getInstance();
    private static MessageManager messageManager = MessageManager.getInstance();

    private XmlParser() {
        // Do nothing
    }

    /**
     * variable.xml 파일을 파싱하는 메서드
     */
    public static boolean readVariableXmlFile() {
        try {
            // 1. read xml file
            File xmlFile = new File(config.getCommonVariableFile());

            Document document = createDocument(xmlFile);
            if(document == null) {
                return false;
            }

            Element documentElement = document.getDocumentElement();
            documentElement.normalize();

            // 2. variables tag 확인
            String rootElement = documentElement.getNodeName();
            if (!rootElement.equals(VARIABLES_TAG)) {
                log.warn("root element is not {} : {}", VARIABLES_TAG, rootElement);
                return false;
            }

            // 3. 각 variable 파싱
            variableTagParsing(documentElement);
            log.debug("{} Variable parsing... (OK)", config.getCommonVariableFile());
            return true;
        } catch (Exception e) {
            log.error("XmlParser.readVariableXmlFile", e);
            return false;
        }
    }


    /**
     * scenario.xml 파일을 파싱하는 메서드
     */
    public static boolean readScenarioXmlFile(){
        try {
            // 1. read xml file
            File xmlFile = new File(config.getCommonScenarioFile());

            Document document = createDocument(xmlFile);
            if(document == null) {
                return false;
            }

            Element documentElement = document.getDocumentElement();
            documentElement.normalize();

            // 2. scenarios tag check
            String rootElement = documentElement.getNodeName();
            if (!rootElement.equals(SCENARIOS_TAG)) {
                log.warn("root element is not {} : {}", SCENARIOS_TAG, rootElement);
                return false;
            }

            // 3. scenario tag check
            Map<String, ScenarioInfo> scenarioInfoMap = new ConcurrentHashMap<>();
            NodeList scenarioNode = document.getElementsByTagName(SCENARIO_TAG);
            for (int idx = 0; idx < scenarioNode.getLength(); idx++) {
                ScenarioInfo scenarioInfo = scenarioTagParsing((Element) scenarioNode.item(idx));
                if (scenarioInfo != null && scenarioInfoMap.putIfAbsent(scenarioInfo.getName(), scenarioInfo) != null) {
                    log.warn("scenario name [{}] is already exist", scenarioInfo.getName());
                }
            }

            ScenarioManager.getInstance().setScenarioMap(scenarioInfoMap);

            scenarioInfoMap.forEach( (k, v) -> log.debug("Scenario [{}] : {}", k, v.toString()) );
            log.debug("{} Scenario parsing... (OK)", config.getCommonScenarioFile());
            return true;
        } catch (Exception e) {
            log.error("XmlParser.readScenarioXmlFile", e);
            return false;
        }
    }

    /**
     * xml 문서를 읽어 Document 클래스로 반환하는 메서드
     */
    private static Document createDocument(File path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // XML parsers should not be vulnerable to XXE attacks 을 지키기 위한 옵션 설정
            // XXE attacks : XML 외부 엔티티 공격
            // to be compliant, completely disable DOCTYPE declaration:
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // or completely disable external entities declarations:
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // or prohibit the use of all protocols by external entities:
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            // or disable entity expansion but keep in mind that this doesn't prevent fetching external entities
            // and this solution is not correct for OpenJDK < 13 due to a bug: https://bugs.openjdk.java.net/browse/JDK-8206132
            factory.setExpandEntityReferences(false);

            return factory.newDocumentBuilder().parse(path);
        } catch (Exception e) {
            log.error("XmlParser.createDocument", e);
            return null;
        }

    }

    /**
     * variable.xml 내 variable tag 파싱 메서드
     */
    private static void variableTagParsing(Element document) {
        // variable tag 리스트화
        NodeList variableNode = document.getElementsByTagName(VARIABLE_TAG);

        ConcurrentHashMap<String, VariableInfo> variableMap = new ConcurrentHashMap<>();

        for (int index = 0; index < variableNode.getLength(); index++) {
            Element variableElement = (Element) variableNode.item(index);
            // 필수 attribute name 과 type 읽기 및 검증
            String name = variableElement.getAttribute(NAME_ATTRIBUTE);
            String type = variableElement.getAttribute(TYPE_ATTRIBUTE);
            if(!isVariableType(type)) {
                log.warn("{} is not define type {} -> {}", type, type, VARIABLE_TYPE_STRING);
                type = VARIABLE_TYPE_STRING;
            }
            // 옵션 attribute length 과 format 읽기 및 검증
            String length = variableElement.getAttribute(LENGTH_ATTRIBUTE);
            String format = variableElement.getAttribute(FORMAT_ATTRIBUTE);
            try {
                new SimpleDateFormat(format);
            } catch (IllegalArgumentException e) {
                log.warn("{} is IllegalArgumentException {} -> null", format, format);
                format = "";
            }
            // 변수 객체 생성, length 가 없으면 -1, format 이 없으면 ""
            VariableInfo variableInfo = new VariableInfo(name, type, parseInteger(length, -1), format);
            // 변수명 기준 이미 저장된 변수가 있을 경우 무시
            if(variableMap.putIfAbsent(name, variableInfo) != null) {
                log.warn("variable name [{}] is already exist", name);
            }
        }
        variableFactory.setVariableMap(variableMap);

        variableMap.forEach( (k, v) -> log.debug("Variable [{}] : {}", k, v.toString()) );
    }

    /**
     * scenario.xml 파일 내 scenario tag 파싱 메서드
     */
    private static ScenarioInfo scenarioTagParsing(Element scenarioElement) {
        // scenario 태그의 필수 attribute 읽기 및 검증
        String scenarioName = scenarioElement.getAttribute(NAME_ATTRIBUTE);
        String scenarioCount = scenarioElement.getAttribute(COUNT_ATTRIBUTE);
        String scenarioId = scenarioElement.getAttribute(ID_ATTRIBUTE);
        if (!variableFactory.isVariableKey(scenarioId)) {
            log.warn("scenario [{}] id [{}] is not define variable", scenarioName, scenarioId);
            return null;
        }

        ScenarioInfo scenarioInfo = new ScenarioInfo(scenarioName, parseInteger(scenarioCount, 1), scenarioId);

        // scenario 태그 내 command 태그 리스트 조회
        NodeList commandList = scenarioElement.getElementsByTagName(COMMAND_TAG);
        List<CommandInfo> commandInfoList = new ArrayList<>();
        for (int idx = 0; idx < commandList.getLength(); idx++) {
            Element commandElement = (Element) commandList.item(idx);
            // command 태그 필수 attribute 읽기 및 검증
            String commandType = commandElement.getAttribute(TYPE_ATTRIBUTE);
            if (!isCommandType(commandType)) {
                log.warn("scenario [{}] command type [{}] is not define type.", scenarioName, commandType);
                return null;
            }
            CommandInfo commandInfo = new CommandInfo(commandType);
            // command type 별 옵션 attribute 읽기
            // command type : pause
            if(commandType.equals(COMMAND_TYPE_PAUSE)) {
                String commandMs = commandElement.getAttribute(MS_ATTRIBUTE);
                commandInfo.setPauseTime(parseInteger(commandMs, 1000));
            }
            // command type : send or recv
            else {
                String commandName = commandElement.getAttribute(NAME_ATTRIBUTE);
                if (!messageManager.isMessageKey(commandName)) {
                    log.warn("scenario [{}] command name [{}] is not define message", scenarioName, commandName);
                    return null;
                }
                commandInfo.setName(commandName);

                NodeList variableList = commandElement.getElementsByTagName(VARIABLE_TAG);
                // command 내 variable 태그 읽기
                Map<String, String> variableMap = getVariableByCommandTag(commandName, variableList);

                commandInfo.setValueMap(variableMap);
            }
            commandInfoList.add(commandInfo);
        }
        scenarioInfo.setCommandInfoList(commandInfoList);
        return scenarioInfo;
    }

    /**
     * scenario.xml 파일 내 variable tag 파싱 메서드
     */
    private static Map<String, String> getVariableByCommandTag(String commandName, NodeList variableList) {
        Map<String, String> variableMap = new HashMap<>();

        for (int idx = 0; idx < variableList.getLength(); idx++) {
            // variable name 이 실제 변수 객체 맵 내에 존재하는지 확인
            Element variableElement = (Element) variableList.item(idx);
            String name = variableElement.getAttribute(NAME_ATTRIBUTE);
            if(!variableFactory.isVariableKey(name)) {
                log.warn("command [{}] id [{}] is not define variable", commandName, name);
                return Collections.emptyMap();
            }
            String value = variableElement.getAttribute(VALUE_ATTRIBUTE);

            if(variableMap.putIfAbsent(name, value) != null) {
                log.warn("variable name [{}] is already exist", name);
            }
        }

        return variableMap;
    }
}
