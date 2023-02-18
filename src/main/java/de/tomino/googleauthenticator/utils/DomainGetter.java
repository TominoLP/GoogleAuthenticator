package de.tomino.googleauthenticator.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class DomainGetter {

    private DomainGetter() {
    }

    @NotNull
    public static String getServersDomain() {
        String ipAddress = Bukkit.getServer().getIp();
        if (ipAddress.equals("")) {
            Bukkit.getLogger().log(Level.INFO, "Can't get ip from Server.properties using Server as domain");
            return "Server";
        } else {
            Bukkit.getLogger().log(Level.INFO, "Using ip from Server.properties as domain");
            URL url = null;
            try {
                url = new URL("http://" + ipAddress);
            } catch (MalformedURLException exception) {
                Bukkit.getLogger().log(Level.INFO, "Can't get the domain: ", exception);
                exception.printStackTrace();
            }
            if (url == null) return "Server";
            return url.getHost();
        }
    }
}
