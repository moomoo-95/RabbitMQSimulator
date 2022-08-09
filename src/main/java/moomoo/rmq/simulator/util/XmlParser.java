package moomoo.rmq.simulator.util;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.config.UserConfig;
import moomoo.rmq.simulator.module.variable.VariableInfo;
import moomoo.rmq.simulator.module.variable.VariableManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

import static moomoo.rmq.simulator.module.base.VariableType.VARIABLE_TYPE_STRING;
import static moomoo.rmq.simulator.module.base.VariableType.isVariableType;
import static moomoo.rmq.simulator.util.CommonUtil.isInteger;

/**
 * @class XmlParser
 * @brief 메인 시나리오 파일을 파싱하는 xml 파서
 * @author hyeon seong lim
 */
@Slf4j
public class XmlParser {

    private static final String VARIABLES_TAG = "variables";
    private static final String VARIABLE_TAG = "variable";

    private static final String NAME_ATTRIBUTE = "name";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String LENGTH_ATTRIBUTE = "length";
    private static final String FORMAT_ATTRIBUTE = "format";

    private static UserConfig config = AppInstance.getInstance().getConfig();

    private XmlParser() {
        // Do nothing
    }

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

            // 2. variables tag check
            String rootElement = documentElement.getNodeName();
            if (!rootElement.equals(VARIABLES_TAG)) {
                log.warn("root element is not {} : {}", VARIABLES_TAG, rootElement);
                return false;
            }

            variableTagParsing(documentElement);
            log.debug("{} Variable parsing... (OK)", config.getCommonVariableFile());
            return true;
        } catch (Exception e) {
            log.error("XmlParser.readXmlFile", e);
            return false;
        }
    }

    private static Document createDocument(File path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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

    private static void variableTagParsing(Element document) {
        // 1. variable tag check
        NodeList variableNode = document.getElementsByTagName(VARIABLE_TAG);

        ConcurrentHashMap<String, VariableInfo> variableMap = new ConcurrentHashMap<>();

        for (int index = 0; index < variableNode.getLength(); index++) {
            Element variableElement = (Element) variableNode.item(index);
            // essential
            String name = variableElement.getAttribute(NAME_ATTRIBUTE);
            String type = variableElement.getAttribute(TYPE_ATTRIBUTE);
            if(!isVariableType(type)) {
                log.warn("{} is not define type {} -> {}", type, type, VARIABLE_TYPE_STRING);
                type = VARIABLE_TYPE_STRING;
            }
            // optional
            String length = variableElement.getAttribute(LENGTH_ATTRIBUTE);
            if (!isInteger(length)) length = "-1";
            String format = variableElement.getAttribute(FORMAT_ATTRIBUTE);
            try {
                new SimpleDateFormat(format);
            } catch (IllegalArgumentException e) {
                log.warn("{} is IllegalArgumentException {} -> null", format, format);
                format = "";
            }

            VariableInfo variableInfo = new VariableInfo(name, type, Integer.parseInt(length), format);

            if(variableMap.putIfAbsent(name, variableInfo) != null) {
                log.warn("variable name [{}] is already exist", name);
            }
        }
        VariableManager.getInstance().setVariableMap(variableMap);

        variableMap.forEach( (k, v) -> log.debug("{} : {}", k, v.toString()) );
    }
}
