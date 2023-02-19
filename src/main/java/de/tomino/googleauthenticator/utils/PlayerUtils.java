package de.tomino.googleauthenticator.utils;

import de.tomino.googleauthenticator.GoogleAuthenticator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class PlayerUtils {

    private final GoogleAuthenticator main;

    public PlayerUtils(GoogleAuthenticator main) {
        this.main = main;
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
}
