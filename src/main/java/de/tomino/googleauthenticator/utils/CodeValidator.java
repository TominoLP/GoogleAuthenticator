package de.tomino.googleauthenticator.utils;

import org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.logging.Level;

public class CodeValidator {

    private CodeValidator() {
    }

    public static boolean validateCode(@NotNull String secret, @NotNull String code) {
        final Base32 base32 = new Base32();
        byte[] decodedKey = base32.decode(secret);
        long time = Instant.now().getEpochSecond() / 30;
        byte[] data = new byte[8];
        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (time & 0xff);
            time >>= 8;
        }
        final SecretKeySpec signingKey = new SecretKeySpec(decodedKey, "HmacSHA1");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Mac can't be created: ", exception);
            exception.printStackTrace();
        }

        if (mac == null) return false;
        byte[] hash = mac.doFinal(data);
        int offset = hash[hash.length - 1] & 0xf;
        int value = ((hash[offset] & 0x7f) << 24) |
                ((hash[offset + 1] & 0xff) << 16) |
                ((hash[offset + 2] & 0xff) << 8) |
                (hash[offset + 3] & 0xff);

        int digits = 6;
        int mod = (int) Math.pow(10, digits);
        return value % mod == Integer.parseInt(code);
    }

}