package nl.sbdeveloper.vehiclesplus.sbutils;

import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

/**
 * Update class for SBDevelopment
 *
 * @author Stijn [SBDeveloper]
 * @version 2.1 [19-11-2021] - This class supports both the v2 Spiget and v2 SBDUpdate API
 *
 * <p>&copy; Stijn Bannink [stijnbannink23@gmail.com] - All rights reserved.</p>
 * @since 05-03-2020
 */
public class UpdateManager {
    private static final JsonParser parser = new JsonParser();

    private static final String SPIGOT_API = "https://api.spiget.org/v2/resources/%s/versions/latest";
    private static final String SPIGOT_DOWNLOAD = "https://api.spiget.org/v2/resources/%s/download";

    private static final String SBDPLUGINS_API = "https://updates.sbdplugins.nl/api/v2/plugins/%d";
    private static final String SBDPLUGINS_DOWNLOAD = "https://updates.sbdplugins.nl/api/v2/download/%d";

    private final Plugin plugin;
    private final Version currentVersion;
    private final int resourceID;
    private final CheckType type;
    private final String license;

    private BiConsumer<VersionResponse, Version> versionResponse;
    private BiConsumer<DownloadResponse, String> downloadResponse;

    /**
     * Construct a new UpdateManager for Spigot
     *
     * @param plugin     The javaplugin (Main class)
     * @param resourceID The resourceID on spigot/sbdplugins
     */
    public UpdateManager(Plugin plugin, int resourceID) {
        this.plugin = plugin;
        this.currentVersion = new Version(plugin.getDescription().getVersion());
        this.resourceID = resourceID;
        this.type = CheckType.SPIGOT;
        this.license = null;
    }

    /**
     * Construct a new UpdateManager for SBDPlugins
     *
     * @param plugin     The javaplugin (Main class)
     * @param resourceID The resourceID on spigot/sbdplugins
     * @param license    The license for the download
     */
    public UpdateManager(Plugin plugin, int resourceID, String license) {
        this.plugin = plugin;
        this.currentVersion = new Version(plugin.getDescription().getVersion());
        this.resourceID = resourceID;
        this.type = CheckType.SBDPLUGINS;
        this.license = license;
    }

    /**
     * Handle the response given by check();
     *
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
                    HttpsURLConnection con = (HttpsURLConnection) new URL(String.format(SBDPLUGINS_API, this.resourceID)).openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");

                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                }

                if (in == null) return;

                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String version = parser.parse(response.toString()).getAsJsonObject().get(type == CheckType.SPIGOT ? "name" : "version").getAsString();
                if (version == null) return;

                Version onlineVersion = new Version(version);

                VersionResponse verRes = this.currentVersion.check(onlineVersion);

                Bukkit.getScheduler().runTask(this.plugin, () -> this.versionResponse.accept(verRes, onlineVersion));
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(this.plugin, () -> this.versionResponse.accept(VersionResponse.UNAVAILABLE, null));
            }
        });
    }

    public void runUpdate() {
        File pluginFile = getPluginFile(); // /plugins/XXX.jar
        if (pluginFile == null) {
            this.downloadResponse.accept(DownloadResponse.ERROR, null);
            Bukkit.getLogger().info("Pluginfile is null");
            return;
        }
        File updateFolder = Bukkit.getUpdateFolderFile();
        if (!updateFolder.exists()) {
            if (!updateFolder.mkdirs()) {
                this.downloadResponse.accept(DownloadResponse.ERROR, null);
                Bukkit.getLogger().info("Updatefolder doesn't exists, and can't be made");
                return;
            }
        }
        final File updateFile = new File(updateFolder, pluginFile.getName());

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            ReadableByteChannel channel;
            try {
                //https://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java
                int response;
                InputStream stream;
                HttpsURLConnection connection;
                if (type == CheckType.SBDPLUGINS) {
                    connection = (HttpsURLConnection) new URL(String.format(SBDPLUGINS_DOWNLOAD, this.resourceID)).openConnection();

                    String urlParameters = "license=" + license + "&port=" + Bukkit.getPort();
                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                    int postDataLength = postData.length;

                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.setDoOutput(true);

                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.write(postData);
                    wr.close();
                } else {
                    connection = (HttpsURLConnection) new URL(String.format(SPIGOT_DOWNLOAD, this.resourceID)).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                }

                response = connection.getResponseCode();
                stream = connection.getInputStream();

                if (response != 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(stream));

                    String inputLine;
                    StringBuilder responsestr = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        responsestr.append(inputLine);
                    }
                    in.close();

                    throw new RuntimeException("Download returned status #" + response, new Throwable(responsestr.toString()));
                }

                channel = Channels.newChannel(stream);
            } catch (IOException e) {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.downloadResponse.accept(DownloadResponse.ERROR, null));
                e.printStackTrace();
                return;
            }

            FileChannel fileChannel = null;
            try {
                FileOutputStream fosForDownloadedFile = new FileOutputStream(updateFile);
                fileChannel = fosForDownloadedFile.getChannel();

                fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.downloadResponse.accept(DownloadResponse.ERROR, null));
                e.printStackTrace();
                return;
            } finally {
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException ioe) {
                        System.out.println("Error while closing response body channel");
                    }
                }

                if (fileChannel != null) {
                    try {
                        fileChannel.close();
                    } catch (IOException ioe) {
                        System.out.println("Error while closing file channel for downloaded file");
                    }
                }
            }

            Bukkit.getScheduler().runTask(this.plugin, () -> this.downloadResponse.accept(DownloadResponse.DONE, updateFile.getPath()));
        });
    }

    private File getPluginFile() {
        if (!(this.plugin instanceof JavaPlugin)) {
            return null;
        }
        try {
            Method method = JavaPlugin.class.getDeclaredMethod("getFile");
            method.setAccessible(true);
            return (File) method.invoke(this.plugin);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not get plugin file", e);
        }
    }

    private enum CheckType {
        SPIGOT, SBDPLUGINS
    }

    public enum VersionResponse {
        LATEST, //Latest version
        FOUND_NEW, //Newer available
        THIS_NEWER, //Local version is newer?
        UNAVAILABLE //Error
    }

    public enum DownloadResponse {
        DONE, ERROR, UNAVAILABLE
    }

    public static class Version {

        private final String version;

        private Version(String version) {
            if (version == null)
                throw new IllegalArgumentException("Version can not be null");
            if (!version.matches("[0-9]+(\\.[0-9]+)*"))
                throw new IllegalArgumentException("Invalid version format");
            this.version = version;
        }

        public final String get() {
            return this.version;
        }

        private VersionResponse check(Version that) {
            String[] thisParts = this.get().split("\\.");
            String[] thatParts = that.get().split("\\.");

            int length = Math.max(thisParts.length, thatParts.length);
            for (int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
                if (thisPart < thatPart)
                    return VersionResponse.FOUND_NEW;
                if (thisPart > thatPart)
                    return VersionResponse.THIS_NEWER;
            }
            return VersionResponse.LATEST;
        }
    }
}
