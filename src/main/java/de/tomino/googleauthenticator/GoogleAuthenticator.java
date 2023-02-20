package de.tomino.googleauthenticator;

import de.tomino.googleauthenticator.comamnds.ForceVerify;
import de.tomino.googleauthenticator.comamnds.RemovePlayer;
import de.tomino.googleauthenticator.events.PlayerJoin;
import de.tomino.googleauthenticator.utils.ConfigHandler;
import de.tomino.googleauthenticator.utils.KeyHandler;
import de.tomino.googleauthenticator.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class GoogleAuthenticator extends JavaPlugin {

    private final PlayerUtils playerUtils = new PlayerUtils(this);

    private KeyHandler keyHandler;

    public static final String version = "0.1.2";

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Loading GoogleAuthenticator...");
        Bukkit.getLogger().info("""
                        \n
                        ██████╗ ██╗   ██╗████████╗██╗  ██╗      ███████╗██╗   ██╗███████╗
                        ██╔══██╗██║   ██║╚══██╔══╝██║  ██║      ██╔════╝╚██╗ ██╔╝██╔════╝
                        ███████║██║   ██║   ██║   ███████║█████╗███████╗ ╚████╔╝ ███████╗
                        ██╔══██║██║   ██║   ██║   ██╔══██║╚════╝╚════██║  ╚██╔╝  ╚════██║
                        ██║  ██║╚██████╔╝   ██║   ██║  ██║      ███████║   ██║   ███████║
                        ╚═╝  ╚═╝ ╚═════╝    ╚═╝   ╚═╝  ╚═╝      ╚══════╝   ╚═╝   ╚══════╝
                                    """);
        Bukkit.getLogger().info("GoogleAuthenticator " + "v" + version + " by Tomino");
        ConfigHandler.loadConfig();

        // Create the Config System
        this.keyHandler = new KeyHandler(this);

        // Registering the Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents( new PlayerUtils(this), this);

        // Registering the Commands
        Objects.requireNonNull(this.getCommand("removePlayer")).setExecutor(new RemovePlayer(this));
        Objects.requireNonNull(this.getCommand("forceAuth")).setExecutor(new ForceVerify(this));
    }

    @Override
    public void onDisable() {

    }

    @NotNull
    public PlayerUtils getPlayerUtils() {
        return playerUtils;
    }

    @NotNull
    public KeyHandler getKeyHandler() {
        return keyHandler;
    }
}
