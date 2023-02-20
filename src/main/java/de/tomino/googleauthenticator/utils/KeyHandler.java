package de.tomino.googleauthenticator.utils;

import org.apache.commons.codec.binary.Base32;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.logging.Level;

public class KeyHandler {

    private final File keyFile;
    private final YamlConfiguration keyConfig;

    private static final SecureRandom random = new SecureRandom();

    public KeyHandler(JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.keyFile = Paths.get(plugin.getDataFolder().getAbsolutePath(), "players.yml").toFile();
        if (!this.keyFile.exists()) {
            try {
                this.keyFile.createNewFile();
            } catch (IOException exception) {
                plugin.getLogger().log(Level.SEVERE, "Config File can't be created: ", exception);
                exception.printStackTrace();
                plugin.getLogger().log(Level.SEVERE, "Plugin will shut down now!");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
        this.keyConfig = YamlConfiguration.loadConfiguration(this.keyFile);
    }

    /**
     * Generates a random Security Key
     *
     * @return the Security Key as a String
     */
    @NotNull
    public static String generateSecretKey() {
        byte[] bytes = new byte[20];
        KeyHandler.random.nextBytes(bytes);
        final Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    /**
     * Save the secret key of the player
     *
     * @param uuid      The UUID of the player
     * @param secretKey The secret key of the player
     */

    public void saveKey(UUID uuid, String secretKey) {
        this.keyConfig.set(uuid.toString(), secretKey);
        this.saveConfig();
    }

    /**
     * Get the secret key of the player
     *
     * @param uuid The UUID of the player
     * @return The secret key of the player
     */

    public String getKey(UUID uuid) {
        return this.keyConfig.getString(uuid.toString(), null);
    }

    /**
     * Save the config
     */

    private void saveConfig() {
        try {
            this.keyConfig.save(this.keyFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Remove the player from the config
     *
     * @param uuid The UUID of the player
     */

    public void removePlayer(UUID uuid) {
        this.keyConfig.set(uuid.toString(), null);
        this.saveConfig();
    }


}