package moomoo.rmq.simulator.config;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserConfig {
    private static final Logger log = LoggerFactory.getLogger(UserConfig.class);

    private static final String CONFIG_LOG = "Lod [{}] config ok.";

    private Ini ini = null;
    // SECTION
    private static final String SECTION_RMQ = "RMQ";
    // FIELD
    // RMQ
    private static final String FILED_RMQ_HOST = "HOST";
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
    private static final String FIELD_RMQ_TIME_OUT = "TIME_OUT";

}
