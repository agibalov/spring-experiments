package io.agibalov;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProgrammaticEncryptionTest {
    private final static String THE_SENSITIVE_DATA = "qwerty";

    @Test
    public void canDecryptWhenUsingCorrectPassword() {
        String encrypted;
        {
            StringEncryptor encryptor = makeEncryptor("password");
            encrypted = encryptor.encrypt(THE_SENSITIVE_DATA);
        }
        assertNotEquals(THE_SENSITIVE_DATA, encrypted);

        StringEncryptor encryptor = makeEncryptor("password");
        String decrypted = encryptor.decrypt(encrypted);
        assertEquals(THE_SENSITIVE_DATA, decrypted);
    }

    @Test
    public void canNotDecryptWhenUsingIncorrectPassword() {
        String encrypted;
        {
            StringEncryptor encryptor = makeEncryptor("password1");
            encrypted = encryptor.encrypt(THE_SENSITIVE_DATA);
        }
        assertNotEquals(THE_SENSITIVE_DATA, encrypted);

        StringEncryptor encryptor = makeEncryptor("password2");
        try {
            encryptor.decrypt(encrypted);
            fail();
        } catch (EncryptionOperationNotPossibleException e) {
            // intentionally blank
        }
    }

    private static StringEncryptor makeEncryptor(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setPoolSize("1");

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);

        return encryptor;
    }
}
