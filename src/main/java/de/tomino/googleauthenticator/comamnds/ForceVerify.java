package de.tomino.googleauthenticator.comamnds;

import de.tomino.googleauthenticator.GoogleAuthenticator;
import de.tomino.googleauthenticator.utils.ConfigHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ForceVerify implements CommandExecutor {

    private final GoogleAuthenticator main;

    public ForceVerify(GoogleAuthenticator main) {
        this.main = main;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("googleauthenticator.forceverify")) {
            if (args.length != 1) {
                sender.sendMessage(ConfigHandler.PROVIDEPLAYER);
                return true;
            }
            Player target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("The player is not online.");
                return true;
            }
            this.main.getPlayerUtils().authPlayer(target);
        } else sender.sendMessage(ConfigHandler.NOPERMISSION);
        return false;
    }
}
