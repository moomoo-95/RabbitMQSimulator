package moomoo.rmq.simulator.config;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.util.PasswordEncryptor;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.util.CommonUtil;
import org.ini4j.Ini;

import java.io.File;
import java.util.function.Predicate;

@Slf4j
public class UserConfig {

    private static final String CONFIG_LOG = "Load [{}] config ok.";

    private Ini ini = null;
    // SECTION
    private static final String SECTION_COMMON = "COMMON";
    private static final String SECTION_RMQ = "RMQ";
    // FIELD
    // COMMON
    private static final String FIELD_COMMON_VARIABLE_FILE = "VARIABLE_FILE";
    private static final String FIELD_COMMON_SCENARIO_FILE = "SCENARIO_FILE";
    private static final String FIELD_COMMON_MSG_PATH = "MSG_PATH";
    // RMQ
    private static final String FIELD_RMQ_HOST = "HOST";
    private static final String FIELD_RMQ_USER = "USER";
    private static final String FIELD_RMQ_PORT = "PORT";
    private static final String FIELD_RMQ_PASS = "PASS";
    private static final String FIELD_RMQ_LOCAL_Q = "LOCAL_Q";
    private static final String FIELD_RMQ_TARGET_Q = "TARGET_Q";
    private static final String FIELD_RMQ_AUTO_RECOVERY = "AUTO_RECOVERY";
    private static final String FIELD_RMQ_NET_RECOVERY = "NET_RECOVERY";
    private static final String FIELD_RMQ_REQ_HB = "REQ_HB";
    private static final String FIELD_RMQ_CONN_TIMEOUT = "CONN_TIMEOUT";
    private static final String FIELD_RMQ_THREAD_SIZE = "THREAD_SIZE";
    private static final String FIELD_RMQ_QUEUE_SIZE = "QUEUE_SIZE";
    private static final String FIELD_RMQ_TIMEOUT = "TIMEOUT";

    // COMMON
    private String commonVariableFile;
    private String commonScenarioFile;
    private String commonMsgPath;
    // RMQ
    private String rmqHost;
    private String rmqUser;
    private int rmqPort;
    private String rmqPass;
    private String rmqLocalQueue;
    private String rmqTargetQueue;
    private boolean rmqAutoRecovery;
    private int rmqNetRecovery;
    private int rmqReqHb;
    private int rmqConnTimeout;
    private int rmqThreadSize;
    private int rmqQueueSize;
    private int rmqTimeout;

    public UserConfig(String configPath) {
        File iniFile = new File(configPath);
        if (!iniFile.isFile() || !iniFile.exists()) {
            log.warn("Not found the config path. (path={})", configPath);
            return;
        }

        try {
            this.ini = new Ini(iniFile);

            loadCommonConfig();
            loadRmqConfig();
        } catch (Exception e) {
            log.error("UserConfig ", e);
        }
    }

    private void loadCommonConfig() {
        this.commonVariableFile = getIniValue(SECTION_COMMON, FIELD_COMMON_VARIABLE_FILE);
        this.commonScenarioFile = getIniValue(SECTION_COMMON, FIELD_COMMON_SCENARIO_FILE);
        this.commonMsgPath = getIniValue(SECTION_COMMON, FIELD_COMMON_MSG_PATH);
    }

    private void loadRmqConfig() {
        this.rmqHost = getIniValue(SECTION_RMQ, FIELD_RMQ_HOST);
        this.rmqUser = getIniValue(SECTION_RMQ, FIELD_RMQ_USER);
        this.rmqPort = Integer.parseInt(getIniValue(SECTION_RMQ, FIELD_RMQ_PORT, CommonUtil::isInteger));
        this.rmqPass = getIniValue(SECTION_RMQ, FIELD_RMQ_PASS);
        try {
            PasswordEncryptor decryptor = new PasswordEncryptor(AppInstance.KEY, AppInstance.ALGORITHM);
            this.rmqPass = decryptor.decrypt(rmqPass);
        } catch (Exception e) {
            log.error("RMQ Password is not available", e);
        }

        this.rmqLocalQueue = getIniValue(SECTION_RMQ, FIELD_RMQ_LOCAL_Q);
        this.rmqTargetQueue = getIniValue(SECTION_RMQ, FIELD_RMQ_TARGET_Q);
        this.rmqAutoRecovery = Boolean.parseBoolean(getIniValue(SECTION_RMQ, FIELD_RMQ_AUTO_RECOVERY));
        this.rmqNetRecovery = Integer.parseInt(getIniValue(SECTION_RMQ, FIELD_RMQ_NET_RECOVERY, CommonUtil::isInteger));
        this.rmqReqHb = Integer.parseInt(getIniValue(SECTION_RMQ, FIELD_RMQ_REQ_HB, CommonUtil::isInteger));
        this.rmqConnTimeout = Integer.parseInt(getIniValue(SECTION_RMQ, FIELD_RMQ_CONN_TIMEOUT, CommonUtil::isInteger));
        this.rmqThreadSize = Integer.parseInt(getIniValue(SECTION_RMQ, FIELD_RMQ_THREAD_SIZE, CommonUtil::isInteger));
        this.rmqQueueSize = Integer.parseInt(getIniValue(SECTION_RMQ, FIELD_RMQ_QUEUE_SIZE, CommonUtil::isInteger));
        this.rmqTimeout = Integer.parseInt(getIniValue(SECTION_RMQ, FIELD_RMQ_TIMEOUT, CommonUtil::isInteger));
        log.debug(CONFIG_LOG, SECTION_RMQ);
    }

    /**
     * config 값을 읽어 특정 형식으로 변환 가능한지 확인한 이후 String 으로 반환하는 메서드
     */
    private String getIniValue(String section, String key, Predicate<String> validation){
        String value = ini.get(section, key);

        if (value == null || !validation.test(value)) {
            log.error("[{}] \"{}\" is null or type mismatch.", section, key);
            System.exit(1);
            return null;
        }

        value = value.trim();
        log.debug("Get [{}] config [{}] : [{}]", section, key, value);
        return  value;
    }

    /**
     * config 값을 읽어 String 으로 반환하는 메서드
     */
    private String getIniValue(String section, String key){
        String value = ini.get(section, key);
        if (value == null) {
            log.error("[{}] \"{}\" is null.", section, key);
            System.exit(1);
            return null;
        }

        value = value.trim();
        log.debug("Get [{}] config [{}] : [{}]", section, key, value);
        return  value;
    }

    private void setIniValue(String section, String key, String value) {
        try {
            ini.put(section, key, value);
            ini.store();

            log.debug("Set [{}] config [{}] : [{}]", section, key, value);
        } catch (Exception e) {
            log.warn("Fail to set [{}] config [{}] : [{}] ", section, key, value);
        }
    }

    // common
    public String getCommonVariableFile() {
        return commonVariableFile;
    }

    public String getCommonMsgPath() {
        return commonMsgPath;
    }

    public String getCommonScenarioFile() {
        return commonScenarioFile;
    }

    // rmq
    public String getRmqHost() {
        return rmqHost;
    }

    public String getRmqUser() {
        return rmqUser;
    }

    public int getRmqPort() {
        return rmqPort;
    }

    public String getRmqPass() {
        return rmqPass;
    }

    public String getRmqLocalQueue() {
        return rmqLocalQueue;
    }

    public String getRmqTargetQueue() {
        return rmqTargetQueue;
    }

    public boolean isRmqAutoRecovery() {
        return rmqAutoRecovery;
    }

    public int getRmqNetRecovery() {
        return rmqNetRecovery;
    }

    public int getRmqReqHb() {
        return rmqReqHb;
    }

    public int getRmqConnTimeout() {
        return rmqConnTimeout;
    }

    public int getRmqThreadSize() {
        return rmqThreadSize;
    }

    public int getRmqQueueSize() {
        return rmqQueueSize;
    }

    public int getRmqTimeout() {
        return rmqTimeout;
    }
}
