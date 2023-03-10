package de.tomino.googleauthenticator.events;

import de.tomino.googleauthenticator.GoogleAuthenticator;
import de.tomino.googleauthenticator.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.ObjectInputFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoin implements Listener {

    private final GoogleAuthenticator main;
    public static final Map<UUID, String> codes = new HashMap<>();
    private final Map<UUID, Integer> tries = new HashMap<>();
    private final Map<UUID, PlayerInformation> information = new HashMap<>();

    public PlayerJoin(GoogleAuthenticator main) {
        this.main = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        player.resetTitle();
//        this.information.put(player.getUniqueId(), new PlayerInformation(
//                player.getGameMode(),
//                player.getInventory().getItem(4)
//        ));
        player.getInventory().setHeldItemSlot(4);
        this.main.getPlayerUtils().lockPlayer(player);
        final String key = this.main.getKeyHandler().getKey(event.getPlayer().getUniqueId());
        if (key == null) {
            final String secretKey = KeyHandler.generateSecretKey();
            final String link = "otpauth://totp/" + player.getName() + "?secret=" + secretKey
                    + "&issuer=MCAUTH-" + ConfigHandler.SERVERNAME + "&algorithm=SHA1&digits=6&period=30";

            codes.put(player.getUniqueId(), secretKey);
            this.main.getPlayerUtils().giveQrCodeMapToPlayer(player, 4, link);
            player.sendMessage("§3§l-----------------MC-AUTH----------------");
            player.sendMessage("§8Please scan the QR Code with your Google Authenticator App");
            player.sendMessage("§8If you don't have the App, you can use this link to get it:");
            player.sendMessage("§7https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2");
            player.sendMessage("§7https://apps.apple.com/us/app/google-authenticator/id388497605");
            player.sendMessage("§8Please enter the code you get from the app in the chat");
            player.sendMessage("§3§l----------------------------------------");
        } else {
            this.main.getPlayerUtils().authPlayer(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (!codes.containsKey(event.getPlayer().getUniqueId())) return;
        final String code = codes.get(player.getUniqueId());
        if (!CodeValidator.validateCode(code, event.getMessage())) {
            if (!this.tries.containsKey(player.getUniqueId())) {
                this.tries.put(player.getUniqueId(), 0);
            }
            int numTries = this.tries.getOrDefault(player.getUniqueId(), 0) + 1;
            this.tries.put(player.getUniqueId(), numTries);
            if (numTries >= 3) {
                Bukkit.getScheduler().runTask(this.main, () -> player.kickPlayer(ConfigHandler.TIMEOUT));
                return;
            }
            player.sendMessage(ConfigHandler.DENIED);
            event.setCancelled(true);
            return;
        }

        player.sendMessage(ConfigHandler.SUCCESS);
        this.tries.remove(player.getUniqueId());
        Bukkit.getScheduler().runTask(this.main, () -> this.main.getPlayerUtils().unlockPlayer(player));
        player.resetTitle();
        codes.remove(player.getUniqueId());
//        final PlayerInformation playerInformation = this.information.get(player.getUniqueId()).clone();
//        Bukkit.getScheduler().runTask(this.main, () -> player.setGameMode(playerInformation.gameMode()));
//        player.getInventory().setItem(4, playerInformation.itemStack());

        if (this.main.getKeyHandler().getKey(player.getUniqueId()) == null)
            this.main.getKeyHandler().saveKey(player.getUniqueId(), code);

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        codes.remove(event.getPlayer().getUniqueId());
//        final PlayerInformation playerInformation = this.information.get(event.getPlayer().getUniqueId()).clone();
//        event.getPlayer().setGameMode(playerInformation.gameMode());
//        event.getPlayer().getInventory().setItem(4, playerInformation.itemStack());
    }
}
