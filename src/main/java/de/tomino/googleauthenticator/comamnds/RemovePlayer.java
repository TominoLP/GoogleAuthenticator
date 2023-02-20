package de.tomino.googleauthenticator.comamnds;

import de.tomino.googleauthenticator.GoogleAuthenticator;
import de.tomino.googleauthenticator.utils.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RemovePlayer implements CommandExecutor {

    private final GoogleAuthenticator main;

    public RemovePlayer(GoogleAuthenticator main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ConfigHandler.PROVIDEPLAYER);
            return true;
        }
        if (!(sender.hasPermission("googleauthenticator.removeplayer")))
            sender.sendMessage(ConfigHandler.NOPERMISSION);
        OfflinePlayer player = main.getServer().getOfflinePlayer(args[0]);
        main.getKeyHandler().removePlayer(player.getUniqueId());
        sender.sendMessage("Player removed successfully. (If the player is online, he will be kicked.)");
        if (player.isOnline())
            Objects.requireNonNull(player.getPlayer()).kickPlayer(ConfigHandler.KICKMESSAGE);
        return true;

    }

}
