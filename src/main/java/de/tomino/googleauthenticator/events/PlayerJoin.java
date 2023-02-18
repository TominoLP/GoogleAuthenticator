package de.tomino.googleauthenticator.events;

import de.tomino.googleauthenticator.GoogleAuthenticator;
import de.tomino.googleauthenticator.utils.CodeValidator;
import de.tomino.googleauthenticator.utils.DomainGetter;
import de.tomino.googleauthenticator.utils.PlayerInformation;
import de.tomino.googleauthenticator.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoin implements Listener {

    private final GoogleAuthenticator main;
    public final Map<UUID, String> codes = new HashMap<>();
    private final Map<UUID, PlayerInformation> information = new HashMap<>();

    public PlayerJoin(GoogleAuthenticator main) {
        this.main = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        player.resetTitle();
        this.information.put(player.getUniqueId(), new PlayerInformation(
                player.getGameMode(),
                player.getInventory().getItem(4)
        ));
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setHeldItemSlot(4);
        this.main.getPlayerFreezer().lockPlayer(player);
        final String key = this.main.getKeyHandler().getKey(event.getPlayer().getUniqueId());
        if (key == null) {
            final String secretKey = Utils.generateSecretKey();
            final String link = "otpauth://totp/" + DomainGetter.getServersDomain() + "?secret=" + secretKey
                    + "&issuer=MCAUTH-" + player.getName() + "&algorithm=SHA1&digits=6&period=30";

            this.codes.put(player.getUniqueId(), secretKey);
            this.main.getPlayerUtils().giveQrCodeMapToPlayer(player, 4, link);
            player.sendMessage("§aPlease scan the QR Code with your Google Authenticator App");
            player.sendMessage("§aIf you don't have the App, you can use this link to get it: " +
                    "§ehttps://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2");
            player.sendMessage("§aPlease enter the code you get from the app in the chat");
        } else {
            player.sendTitle("§4ENTER YOU CODE", "§cPlease enter your code in the chat to verify your identity", 10, 100000, 10);
            this.codes.put(player.getUniqueId(), key);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (!this.codes.containsKey(event.getPlayer().getUniqueId())) return;

        final String code = this.codes.get(player.getUniqueId());

        if (!CodeValidator.validateCode(code, event.getMessage())) {
            player.sendMessage("§cYour code is wrong");
            event.setCancelled(true);
            return;
        }

        player.sendMessage("§aYour code is right");
        Bukkit.getScheduler().runTask(this.main, () -> this.main.getPlayerFreezer().unlockPlayer(player));
        player.resetTitle();

        this.codes.remove(player.getUniqueId());
        final PlayerInformation playerInformation = this.information.get(player.getUniqueId()).clone();
        this.codes.remove(player.getUniqueId());

        Bukkit.getScheduler().runTask(this.main, () -> player.setGameMode(playerInformation.gameMode()));
        player.getInventory().setItem(4, playerInformation.itemStack());

        if (this.main.getKeyHandler().getKey(player.getUniqueId()) == null)
            this.main.getKeyHandler().saveKey(player.getUniqueId(), code);

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.codes.remove(event.getPlayer().getUniqueId());
        final PlayerInformation playerInformation = this.information.get(event.getPlayer().getUniqueId()).clone();
        this.codes.remove(event.getPlayer().getUniqueId());

        event.getPlayer().setGameMode(playerInformation.gameMode());
        event.getPlayer().getInventory().setItem(4, playerInformation.itemStack());
    }
}
