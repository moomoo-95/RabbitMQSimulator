import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.module.util.PasswordEncryptor;
import moomoo.rmq.simulator.AppInstance;
import moomoo.rmq.simulator.util.MsgParser;
import moomoo.rmq.simulator.util.XmlParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

@Slf4j
public class UtilTest {
    private static final String PASSWORD = "testpassword";

    private static PasswordEncryptor cryptor;

    @BeforeClass
    public static void setUp() {
        cryptor = new PasswordEncryptor(AppInstance.KEY, AppInstance.ALGORITHM);
    }

    @Test
    public void encryptorTest() {
        String encPasswd = cryptor.encrypt(PASSWORD);
        String decPasswd = cryptor.decrypt(encPasswd);

        log.debug("ori : {}", PASSWORD);
        log.debug("enc : {}", encPasswd);
        log.debug("dec : {}", decPasswd);

    }

    @Test
    public void typeTest() {
        String id = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(id);
//        UUID uuid2 = UUID.fromString("e9af1ba3-c96a-45b5-b06c1185c1728b2e8");
//        UUID uuid3 = UUID.fromString("e9af1ba3-c96a-45b5-b06c-185c172hb2e8");
        log.debug("{} {}", id, id.length());
        log.debug("{} {}", uuid.toString(), uuid.toString().length());
    }

    @Test
    public void xmlParseTest() {
        AppInstance.getInstance().setConfig("src/main/resources/config/user_config.ini");
        XmlParser.readVariableXmlFile();
    }

    @Test
    public void msgParseTest() {
        AppInstance.getInstance().setConfig("src/main/resources/config/user_config.ini");
        XmlParser.readVariableXmlFile();
        MsgParser.readMsgDir();
    }

    @Test
    public void msgParseTest2() {
//        String str = "][]][]][][]]][][]]]]][]]][][]]][][]]][]]][][]dsadsadd]d[qw]d[][saf[]]]" +
//                "[]]][][]][][]][]][]]][][[]][]]][][]v][]][]]][][]v][[][][][]]][][][]]][][]]]" +
//                "][][][]][]]]][]]][][][][]][]]][][]v][[][]]][][]]]][]][]]][][]vgregrgrrs][][" +
//                "]]][][]][]]][][][]][da]sd[as]f][af[as][saf]asf[]as[f]af[]f][]][][]]][]]][][" +
//                "[]]][][]][][]][]][]]][][[]][]]][][]v][]][]]][][]v][[][][][]]][][][]]][][]]]" +
//                "][][][]][]]]][]]][][][][]][]]][][]v][[][]]][][]]]][]][]]][][]vgregrgrrs][][" +
//                "]]][][]][]]][][][]][da]sd[as]f][af[as][saf]asf[]as[f]af[]f][]][][]]][]]][][" +
//                "[]]][][]][][]][]][]]][][[]][]]][][]v][]][]]][][]v][[][][][]]][][][]]][][]]]" +
//                "][][][]][]]]][]]][][][][]][]]][][]v][[][]]][][]]]][]][]]][][]vgregrgrrs][][" +
//                "]]][][]][]]][][][]][da]sd[as]f][af[as][saf]asf[]as[f]af[]f][]][][]]][]]][][" +
//                "[]]][][]][][]][]][]]][][[]][]]][][]v][]][]]][][]v][[][][][]]][][][]]][][]]]" +
//                "][][][]][]]]][]]][][][][]][]]][][]v][[][]]][][]]]][]][]]][][]vgregrgrrs][][" +
//                "]]][][]][]]][][][]][da]sd[as]f][af[as][saf]asf[]as[f]af[]f][]][][]]][]]][][" +
//                "[]]][][]][][]][]][]]][][[]][]]][][]v][]][]]][][]v][[][][][]]][][][]]][][]]]" +
//                "][][][]][]]]][]]][][][][]][]]][][]v][[][]]][][]]]][]][]]][][]vgregrgrrs][][" +
//                "]]][][]][]]][][][]][da]sd[as]f][af[as][saf]asf[]as[f]af[]f][]][][]]][]]][][" +
//                "[]]][][]][][]][]][]]][][[]][]]][][]v][]][]]][][]v][[][][][]]][][][]]][][]]]" +
//                "][][][]][]]]][]]][][][][]][]]][][]v][[][]]][][]]]][]][]]][][]vgregrgrrs][][" +
//                "]]][][]][]]][][][]][da]sd[as]f][af[as][saf]asf[]as[f]af[]f][]][][]]][]]][][" +
//                "]][]]][][]][]]][][]v][]]][][]][]]]dasdasdasdsadsdadssssdsa[][]][]]][][]vv[]";
//
//        log.debug("start");
//        long start1 = System.currentTimeMillis();
//        for(int idx = 0; idx < 100000; idx++) {
//            MsgParser.findVariableIndex(str);
//        }
//        long end1 = System.currentTimeMillis();
//
//        log.debug("a : {}", (end1 - start1));
//        long start2 = System.currentTimeMillis();
//        for(int idx = 0; idx < 100000; idx++) {
//            MsgParser.findVariableIndex2(str);
//        }
//        long end2 = System.currentTimeMillis();
//
//        log.debug("b : {}", (end2 - start2));
    }
}
