package de.tomino.googleauthenticator.utils;

import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

public class Utils {

    private static final SecureRandom random = new SecureRandom();

    private Utils() {
    }

    /**
     * Generates a random Security Key
     *
     * @return the Security Key as a String
     */
    @NotNull
    public static String generateSecretKey() {
        byte[] bytes = new byte[20];
        Utils.random.nextBytes(bytes);
        final Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }
}
