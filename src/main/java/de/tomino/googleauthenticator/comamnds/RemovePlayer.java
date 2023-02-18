package de.tomino.googleauthenticator.comamnds;

import de.tomino.googleauthenticator.GoogleAuthenticator;
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
            sender.sendMessage("Please provide a player name.");
            return true;
        }
        if (!(sender.hasPermission("googleauthenticator.removeplayer")))
            sender.sendMessage("You don't have the permission to do this.");
        OfflinePlayer player = main.getServer().getOfflinePlayer(args[0]);
        main.getKeyHandler().removePlayer(player.getUniqueId());
        sender.sendMessage("Player removed successfully. (If the player is online, he will be kicked.");
        if (player.isOnline())
            Objects.requireNonNull(player.getPlayer()).kickPlayer("You have been removed from the Authenticator system.");
        return true;

    }

}
