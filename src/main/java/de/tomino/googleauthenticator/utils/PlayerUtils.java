package de.tomino.googleauthenticator.utils;

import de.tomino.googleauthenticator.GoogleAuthenticator;
import de.tomino.googleauthenticator.events.PlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerUtils implements Listener {

    private final GoogleAuthenticator main;

    public PlayerUtils(GoogleAuthenticator main) {
        this.main = main;
    }

    private final Map<Player, Vector> lockedPlayers = new HashMap<>();

    public boolean isPlayerLocked(@NotNull Player player) {
        return lockedPlayers.containsKey(player);
    }

    /**
     * Gives a Map with a QR code that contains the provided
     * data to the given player
     *
     * @param player The Player that should get the map
     * @param slot   The slot that the map should be in
     * @param data   The data that should be converted to the QR code
     */
    public void giveQrCodeMapToPlayer(@NotNull Player player, int slot, @NotNull String data) {
        final QrCodeGen renderer = new QrCodeGen(data);
        final MapView mapView = Bukkit.createMap(player.getWorld());
        mapView.getRenderers().forEach(mapView::removeRenderer);
        mapView.addRenderer(renderer);
        final ItemStack map = new ItemStack(Material.FILLED_MAP);
        final MapMeta mapMeta = (MapMeta) map.getItemMeta();
        assert mapMeta != null;
        mapMeta.setMapView(mapView);
        map.setItemMeta(mapMeta);
        player.getInventory().setItem(slot, map);
    }

    /**
     * locks the player in place and locks all inventory slots
     *
     * @param player the player that should be locked
     */

    public void lockPlayer(@NotNull Player player) {
        if (isPlayerLocked(player)) return;
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 128, false, false, false));
        player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1, 1);
        lockedPlayers.put(player, this.convertLocationToVector(player.getLocation()));
    }

    /**
     * unlocks the player
     *
     * @param player the player that should be unlocked
     */

    public void unlockPlayer(@NotNull Player player) {
        if (!isPlayerLocked(player)) return;
        player.setFlySpeed(0.1F);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1, 1);
        player.resetTitle();
        lockedPlayers.remove(player);
    }


    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (isPlayerLocked(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().teleport(this.convertVectorToLocation(
                    lockedPlayers.get(event.getPlayer()),
                    event.getPlayer().getWorld(),
                    event.getPlayer().getLocation().getYaw(),
                    event.getPlayer().getLocation().getPitch()
            ));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (isPlayerLocked((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (isPlayerLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }


    private Vector convertLocationToVector(Location location) {
        return new Vector(
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

    private Location convertVectorToLocation(Vector vector, World world, float yaw, float pitch) {
        return new Location(
                world,
                vector.getX(),
                vector.getY(),
                vector.getZ(),
                yaw,
                pitch
        );
    }

    public void authPlayer(Player target) {
        this.main.getPlayerUtils().lockPlayer(target);
        final String key = this.main.getKeyHandler().getKey(target.getUniqueId());
        PlayerJoin.codes.put(target.getUniqueId(), key);
        target.sendTitle("§4ENTER YOU CODE", "§cPlease enter your code in the chat to verify your identity", 10, 100000, 10);
    }


}
