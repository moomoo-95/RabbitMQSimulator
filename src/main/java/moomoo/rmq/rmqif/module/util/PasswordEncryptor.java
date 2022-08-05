package moomoo.rmq.rmqif.module.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

public class PasswordEncryptor {
    StandardPBEStringEncryptor crypto;

    public PasswordEncryptor(String key, String alg) {
        crypto = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setPassword(key);
        config.setAlgorithm(alg);
        crypto.setConfig(config);
    }

    public String encrypt(String pass) {
        return crypto.encrypt(pass);
    }

    public String decrypt(String encrypted) {
        return crypto.decrypt(encrypted);
    }
}