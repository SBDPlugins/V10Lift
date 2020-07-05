package nl.SBDeveloper.V10Lift.sbutils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.BiConsumer;

/**
 * Update class for SBDevelopment
 * @author Stijn [SBDeveloper]
 * @since 05-03-2020
 * @version 1.4
 *
 * © Stijn Bannink <stijnbannink23@gmail.com> - All rights reserved.
 */
public class UpdateManager {

    private static final String SPIGOT_API = "https://api.spigotmc.org/legacy/update.php?resource=%d";

    /* Port 4000 is now legacy, 4443 has a SSL cert */
    /* As of 24-05-2020, using the legacy port because of SSL errors */
    private static final String SBDPLUGINS_API = "http://updates.sbdplugins.nl:4000/api/resources/%d";

    private static final String RESOURCE_DOWNLOAD = "http://api.spiget.org/v2/resources/%s/download";

    private Plugin plugin;
    private Version currentVersion;
    private int resourceID;
    private CheckType type;
    private BiConsumer<VersionResponse, Version> versionResponse;
    private BiConsumer<DownloadResponse, String> downloadResponse;

    /**
     * Construct a new UpdateManager
     *
     * @param plugin The javaplugin (Main class)
     * @param resourceID The resourceID on spigot/sbdplugins
     * @param type The check type
     */
    public UpdateManager(@Nonnull Plugin plugin, int resourceID, CheckType type) {
        this.plugin = plugin;
        this.currentVersion = new Version(plugin.getDescription().getVersion());
        this.resourceID = resourceID;
        this.type = type;
    }

    /**
     * Handle the response given by check();
     * @param versionResponse The response
     * @return The updatemanager
     */
    public UpdateManager handleResponse(BiConsumer<VersionResponse, Version> versionResponse) {
        this.versionResponse = versionResponse;
        return this;
    }

    public UpdateManager handleDownloadResponse(BiConsumer<DownloadResponse, String> downloadResponse) {
        this.downloadResponse = downloadResponse;
        return this;
    }

    /**
     * Check for a new version
     */
    public void check() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                BufferedReader in = null;
                if (type == CheckType.SPIGOT) {
                    HttpsURLConnection con = (HttpsURLConnection) new URL(String.format(SPIGOT_API, this.resourceID)).openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");

                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else if (type == CheckType.SBDPLUGINS) {
                    HttpURLConnection con = (HttpURLConnection) new URL(String.format(SBDPLUGINS_API, this.resourceID)).openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");

                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                }

                if (in == null) return;

                String version = null;

                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (type == CheckType.SPIGOT) {
                    version = response.toString();
                } else if (type == CheckType.SBDPLUGINS) {
                    JsonParser parser = new JsonParser();
                    JsonObject object = parser.parse(response.toString()).getAsJsonObject();

                    version = object.get("data").getAsJsonObject().get("version").getAsString();
                }

                if (version == null) return;

                Version onlineVersion = new Version(version);

                boolean latestVersion = this.currentVersion.compareTo(onlineVersion) < 0;

                Bukkit.getScheduler().runTask(this.plugin, () -> this.versionResponse.accept(latestVersion ? VersionResponse.LATEST : VersionResponse.FOUND_NEW, latestVersion ? this.currentVersion : onlineVersion));
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(this.plugin, () -> this.versionResponse.accept(VersionResponse.UNAVAILABLE, null));
            }
        });
    }

    public void runUpdate() {
        File pluginFile = getPluginFile();// /plugins/XXX.jar
        if (pluginFile == null) {
            this.downloadResponse.accept(DownloadResponse.ERROR, null);
            return;
        }
        File updateFolder = Bukkit.getUpdateFolderFile();
        if (!updateFolder.exists()) {
            if (!updateFolder.mkdirs()) {
                this.downloadResponse.accept(DownloadResponse.ERROR, null);
                return;
            }
        }
        final File updateFile = new File(updateFolder, pluginFile.getName());

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.type == CheckType.SBDPLUGINS) {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.downloadResponse.accept(DownloadResponse.UNAVAILABLE, null));
                return;
            }

            ReadableByteChannel channel;
            try {
                //https://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java
                HttpURLConnection connection = (HttpURLConnection) new URL(String.format(RESOURCE_DOWNLOAD, this.resourceID)).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                if (connection.getResponseCode() != 200) {
                    throw new RuntimeException("Download returned status #" + connection.getResponseCode());
                }
                channel = Channels.newChannel(connection.getInputStream());
            } catch (IOException e) {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.downloadResponse.accept(DownloadResponse.ERROR, null));
                return;
            }
            try {
                FileOutputStream output = new FileOutputStream(updateFile);
                output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                output.flush();
                output.close();
            } catch (IOException e) {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.downloadResponse.accept(DownloadResponse.ERROR, null));
                return;
            }

            Bukkit.getScheduler().runTask(this.plugin, () -> this.downloadResponse.accept(DownloadResponse.DONE, updateFile.getPath()));
        });
    }

    @Nullable
    private File getPluginFile() {
        if (!(this.plugin instanceof JavaPlugin)) { return null; }
        try {
            Method method = JavaPlugin.class.getDeclaredMethod("getFile");
            method.setAccessible(true);
            return (File) method.invoke(this.plugin);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not get plugin file", e);
        }
    }

    public enum CheckType {
        SPIGOT, SBDPLUGINS
    }

    public enum VersionResponse {
        LATEST, FOUND_NEW, UNAVAILABLE
    }

    public enum DownloadResponse {
        DONE, ERROR, UNAVAILABLE
    }

    public static class Version implements Comparable<Version> {

        private String version;

        public final String get() {
            return this.version;
        }

        public Version(String version) {
            if(version == null)
                throw new IllegalArgumentException("Version can not be null");
            if(!version.matches("[0-9]+(\\.[0-9]+)*"))
                throw new IllegalArgumentException("Invalid version format");
            this.version = version;
        }

        @Override
        public int compareTo(@Nonnull Version that) {
            String[] thisParts = this.get().split("\\.");
            String[] thatParts = that.get().split("\\.");

            int length = Math.max(thisParts.length, thatParts.length);
            for (int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
                if(thisPart < thatPart)
                    return -1;
                if(thisPart > thatPart)
                    return 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object that) {
            if (this == that) return true;
            if (that == null) return false;
            if (this.getClass() != that.getClass()) return false;
            return this.compareTo((UpdateManager.Version) that) == 0;
        }
    }
}