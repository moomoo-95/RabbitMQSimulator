import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.module.util.PasswordEncryptor;
import moomoo.rmq.simulator.AppInstance;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class EncryptorTest {
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
}
