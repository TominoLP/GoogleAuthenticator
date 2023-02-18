package de.tomino.googleauthenticator.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class provides methods to manage the secret keys for players.
 */
public class KeyHandler {

    private final YamlConfiguration keyConfig;
    private final MessageDigest sha1;

    /**
     * Constructs a new KeyHandler for the specified plugin.
     *
     * @param plugin the JavaPlugin instance.
     */
    public KeyHandler(JavaPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File keyFile = Paths.get(dataFolder.getAbsolutePath(), "players.yml").toFile();
        if (!keyFile.exists()) {
            try {
                keyFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Config File can't be created: ", e);
                plugin.getLogger().log(Level.SEVERE, "Plugin will shut down now!");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
        keyConfig = YamlConfiguration.loadConfiguration(keyFile);
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            plugin.getLogger().log(Level.SEVERE, "SHA-1 algorithm not supported: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the specified secret key for the specified player UUID in the key file.
     *
     * @param uuid the UUID of the player.
     * @param secretKey the secret key.
     */
    public void saveKey(UUID uuid, String secretKey) {
        byte[] digest = sha1.digest(secretKey.getBytes());
        String encoded = Base64.getEncoder().encodeToString(digest);
        keyConfig.set(uuid.toString(), encoded);
        saveConfig();
    }

    /**
     * Retrieves the secret key for the specified player UUID from the key file.
     *
     * @param uuid the UUID of the player.
     * @return the secret key, or null if not found.
     */
    public String getKey(UUID uuid) {
        String encoded = keyConfig.getString(uuid.toString());
        if (encoded == null) {
            return null;
        }
        byte[] digest = Base64.getDecoder().decode(encoded);
        return new String(digest);
    }

    /**
     * Removes the player UUID and their secret key from the key file.
     *
     * @param uuid the UUID of the player.
     */
    public void removePlayer(UUID uuid) {
        keyConfig.set(uuid.toString(), null);
        saveConfig();
    }

    /**
     * Saves the current state of the key file.
     */
    private void saveConfig() {
        try {
            keyConfig.save(Paths.get(keyConfig.getCurrentPath()).toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save key file", e);
        }
    }
}