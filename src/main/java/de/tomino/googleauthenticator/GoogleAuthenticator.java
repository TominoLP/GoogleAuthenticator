package de.tomino.googleauthenticator;

import de.tomino.googleauthenticator.comamnds.RemovePlayer;
import de.tomino.googleauthenticator.events.PlayerJoin;
import de.tomino.googleauthenticator.utils.KeyHandler;
import de.tomino.googleauthenticator.utils.PlayerFreezer;
import de.tomino.googleauthenticator.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class GoogleAuthenticator extends JavaPlugin {

    private final PlayerUtils playerUtils = new PlayerUtils(this);

    private KeyHandler keyHandler;
    private PlayerFreezer playerFreezer;

    @Override
    public void onEnable() {

        // Create the Config System
        this.keyHandler = new KeyHandler(this);

        // Registering the Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(this.playerFreezer = new PlayerFreezer(), this);

        // Registering the Commands
        Objects.requireNonNull(this.getCommand("removePlayer")).setExecutor(new RemovePlayer(this));
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

    @NotNull
    public PlayerFreezer getPlayerFreezer() {
        return playerFreezer;
    }
}
