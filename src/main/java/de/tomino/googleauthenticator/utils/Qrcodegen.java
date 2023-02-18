package de.tomino.googleauthenticator.utils;

import net.glxn.qrgen.javase.QRCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * A Map Renderer, that renders a QR code by the data
 */
public class Qrcodegen extends MapRenderer {

    private final BufferedImage image;
    private boolean done;

    public Qrcodegen(String data) {
        this.image = this.generateQrCode(data);
    }

    /**
     * Genrate the QR code from the data as a BufferedImage
     *
     * @param data The data that should be displayed as a QR code
     * @return The generated BufferedImage
     * @see BufferedImage
     */
    @Nullable
    private BufferedImage generateQrCode(@NotNull String data) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        QRCode.from(data).withCharset("UTF-8").writeTo(stream);

        final InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Image image = null;
        try {
            image = ImageIO.read(is);
        } catch (IOException exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Map can't be crated: ", exception);
            exception.printStackTrace();
        }

        if (image == null) return null;

        final BufferedImage dest = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB_PRE);
        final Graphics graphics = dest.getGraphics();
        graphics.drawImage(image, 0, 0, 128, 128, null);
        graphics.dispose();

        return dest;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (done) return;
        canvas.drawImage(0, 0, image);
        map.setTrackingPosition(false);
        done = true;
    }
}

