package de.tomino.googleauthenticator.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerFreezer implements Listener {

    private final Map<Player, Vector> lockedPlayers = new HashMap<>();

    public boolean isPlayerLocked(@NotNull Player player) {
        return lockedPlayers.containsKey(player);
    }

    public void lockPlayer(@NotNull Player player) {
        if (isPlayerLocked(player)) return;
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 128, false, false, false));
        player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1, 1);
        lockedPlayers.put(player, this.convertLocationToVector(player.getLocation()));
    }

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

    private void blockrotation() {


    }
}
