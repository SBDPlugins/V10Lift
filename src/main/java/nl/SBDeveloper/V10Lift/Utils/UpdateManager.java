package nl.SBDeveloper.V10Lift.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.BiConsumer;

/**
 * Update class for SBDevelopment
 * @author Stijn [SBDeveloper]
 * @since 12-01-2020
 * @version 1.1
 *
 * © Stijn Bannink <stijnbannink23@gmail.com> - All rights reserved.
 */
public class UpdateManager {

    private static String SPIGOT_API = "http://api.spiget.org/v2/resources/%d/versions?size=1&sort=-releaseDate";
    private static String SBDPLUGINS_API = "http://updates.sbdplugins.nl:4000/api/resources/%d";

    private Plugin plugin;
    private String currentVersion;
    private int resourceID;
    private CheckType type;
    private BiConsumer<VersionResponse, String> versionResponse;

    /**
     * Construct a new UpdateManager
     *s
     * @param plugin The javaplugin (Main class)
     * @param resourceID The resourceID on spigot/sbdplugins
     * @param type The check type
     */
    public UpdateManager(@Nonnull Plugin plugin, int resourceID, CheckType type) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.resourceID = resourceID;
        this.type = type;
    }

    /**
     * Handle the response given by check();
     * @param versionResponse The response
     * @return The updatemanager
     */
    public UpdateManager handleResponse(BiConsumer<VersionResponse, String> versionResponse) {
        this.versionResponse = versionResponse;
        return this;
    }

    /**
     * Check for a new version
     */
    public void check() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                HttpURLConnection con = null;
                if (type == CheckType.SPIGOT) {
                    con = (HttpURLConnection) new URL(String.format(SPIGOT_API, this.resourceID)).openConnection();
                } else if (type == CheckType.SBDPLUGINS) {
                    con = (HttpURLConnection) new URL(String.format(SBDPLUGINS_API, this.resourceID)).openConnection();
                }

                if (con == null) return;

                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                String version = null;

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JsonParser parser = new JsonParser();

                if (type == CheckType.SPIGOT) {
                    JsonArray array = parser.parse(response.toString()).getAsJsonArray();

                    version = array.get(0).getAsJsonObject().get("name").getAsString();
                } else if (type == CheckType.SBDPLUGINS) {
                    JsonObject object = parser.parse(response.toString()).getAsJsonObject();

                    version = object.get("data").getAsJsonObject().get("version").getAsString();
                }

                if (version == null) return;

                boolean latestVersion = version.equalsIgnoreCase(this.currentVersion);

                String finalVersion = version;
                Bukkit.getScheduler().runTask(this.plugin, () -> this.versionResponse.accept(latestVersion ? VersionResponse.LATEST : VersionResponse.FOUND_NEW, latestVersion ? this.currentVersion : finalVersion));
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(this.plugin, () -> this.versionResponse.accept(VersionResponse.UNAVAILABLE, null));
            }
        });
    }

    public enum CheckType {
        SPIGOT, SBDPLUGINS
    }

    public enum VersionResponse {
        LATEST, FOUND_NEW, UNAVAILABLE
    }

}