package nl.SBDeveloper.V10Lift.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class SBYamlFile {
    //SBYamlFile file = new SBYamlFile(this, "data");

    private FileConfiguration fileConfiguration;
    private File file;
    private String name;
    private Plugin pl;

    public SBYamlFile(@Nonnull Plugin pl, String name) {
        this.pl = pl;
        this.name = name;

        if (!pl.getDataFolder().exists()) {
            pl.getDataFolder().mkdir();
        }

        this.file = new File(pl.getDataFolder(), name + ".yml");
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
                Bukkit.getLogger().info("[SBYamlManager] Generating the " + name + ".yml!");
            } catch (IOException e) {
                Bukkit.getLogger().severe("[SBYamlManager] Couldn't generate the " + name + ".yml!");
            }
        }
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void loadDefaults() {
        Bukkit.getLogger().info("[SBYamlManager] Copying default " + name + ".yml to the folder!");
        Reader defConfigStream1 = new InputStreamReader(this.pl.getResource(name + ".yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig1 = YamlConfiguration.loadConfiguration(defConfigStream1);
        getFile().setDefaults(defConfig1);
        getFile().options().copyDefaults(true);
        saveFile();
    }

    public FileConfiguration getFile() {
        return this.fileConfiguration;
    }

    public void saveFile() {
        try {
            this.fileConfiguration.save(this.file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[SBYamlManager] Couldn't save the " + name + ".yml!");
        }
    }

    public void reloadConfig() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }
}
