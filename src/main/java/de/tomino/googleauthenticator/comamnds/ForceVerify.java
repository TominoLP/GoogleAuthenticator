package de.tomino.googleauthenticator.comamnds;

import de.tomino.googleauthenticator.GoogleAuthenticator;
import de.tomino.googleauthenticator.events.PlayerJoin;
import de.tomino.googleauthenticator.utils.PlayerInformation;
import de.tomino.googleauthenticator.utils.Utils;
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
        if (!(sender instanceof Player player)) Utils.log("You can't use this command from the console.", 1);
        else {
            if (player.hasPermission("googleauthenticator.forceverify")) {
                if (args.length != 1) {
                    player.sendMessage("Please provide a player name.");
                    return true;
                }
                Player target = player.getServer().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage("The player is not online.");
                    return true;
                }
                this.main.getPlayerFreezer().lockPlayer(target);
                final String key = this.main.getKeyHandler().getKey(target.getUniqueId());
                PlayerJoin.codes.put(target.getUniqueId(), key);
                target.sendTitle("§4ENTER YOU CODE", "§cPlease enter your code in the chat to verify your identity", 10, 100000, 10);
            } else player.sendMessage("You don't have the permission to do this.");
        }
        return false;
    }
}
