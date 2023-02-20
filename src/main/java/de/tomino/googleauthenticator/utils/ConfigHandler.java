package de.tomino.googleauthenticator.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ConfigHandler {

    private final File configFile;
    private final YamlConfiguration keyConfig;

    public ConfigHandler(JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.configFile = Paths.get(plugin.getDataFolder().getAbsolutePath(), "config.yml").toFile();
        if (!this.configFile.exists()) {
            try {
                this.configFile.createNewFile();
            } catch (IOException exception) {
                plugin.getLogger().log(Level.SEVERE, "Config File can't be created: ", exception);
                exception.printStackTrace();
                plugin.getLogger().log(Level.SEVERE, "Plugin will shut down now!");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
        this.keyConfig = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public static final File CONFIG_FILE = new File("plugins/GoogleAuthenticator/config.yml");
    public static final YamlConfiguration CONFIG = YamlConfiguration.loadConfiguration(CONFIG_FILE);
    public static String SERVERNAME;
    public static String TITLE;
    public static String SUBTITLE;
    public static String SUCCESS;
    public static String DENIED;
    public static String KICKMESSAGE;
    public static String TIMEOUT;
    public static String NOPERMISSION;
    public static String PROVIDEPLAYER;

    public static void loadConfig() {
        CONFIG.options().copyDefaults(true);
        CONFIG.addDefault("ServerName", "Your Server");
        CONFIG.addDefault("Title", "§4ENTER YOU CODE");
        CONFIG.addDefault("Subtitle", "§cPlease enter your code in the chat to verify your identity");
        CONFIG.addDefault("Success", "§l§aYour code is right, Have fun!");
        CONFIG.addDefault("Denied", "§l§cYour code is wrong, please try again!");
        CONFIG.addDefault("KickMessage", "You have been removed from the Authenticator system.");
        CONFIG.addDefault("Timeout", "§cYou entered the wrong code too many times");
        CONFIG.addDefault("NoPermission", "§cYou don't have the permission to do this.");
        CONFIG.addDefault("ProvidePlayer", "§cPlease provide a player name.");

        SERVERNAME = CONFIG.getString("ServerName");
        TITLE = CONFIG.getString("Title");
        SUBTITLE = CONFIG.getString("Subtitle");
        SUCCESS = CONFIG.getString("Success");
        DENIED = CONFIG.getString("Denied");
        KICKMESSAGE = CONFIG.getString("KickMessage");
        TIMEOUT = CONFIG.getString("Timeout");
        NOPERMISSION = CONFIG.getString("NoPermission");
        PROVIDEPLAYER = CONFIG.getString("ProvidePlayer");
        save();

        try {
            CONFIG.save(CONFIG_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public static void save() {
        try {
            CONFIG.save(CONFIG_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
