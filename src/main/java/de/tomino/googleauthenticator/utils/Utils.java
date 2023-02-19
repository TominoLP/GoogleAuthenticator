package de.tomino.googleauthenticator.utils;

import org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

import java.util.logging.Level;

public class Utils {

    private static final SecureRandom random = new SecureRandom();

    private Utils() {
    }

    /**
     * Genrates a random Security Key
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

    public static void log(String message, Integer level) {
        if (level == 0) Bukkit.getLogger().log(Level.INFO, message);
        else if (level == 1) Bukkit.getLogger().log(Level.WARNING, message);
        else if (level == 2) Bukkit.getLogger().log(Level.SEVERE, message);
        else if (level == 3) Bukkit.getLogger().log(Level.FINE, message);
    }
}
