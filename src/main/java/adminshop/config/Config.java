package adminshop.config;

import adminshop.Main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

public class Config {

    private Main plugin;
    private FileConfiguration defaultConfig = null;
    private File configFile = null;

    public Config(Main plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig() {
        if(configFile == null) {
            configFile = new File(plugin.getDataFolder().getPath(), "config.yml");
        }

        defaultConfig = YamlConfiguration.loadConfiguration(configFile);

        save();

        try {
            Reader defConfigStream = new FileReader(configFile);
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                defaultConfig.setDefaults(defConfig);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if(defaultConfig == null) {
            reloadConfig();
        }
        return defaultConfig;
    }

    public void save() {
        if(defaultConfig == null || configFile == null) {
            return;
        }

        try {
            getConfig().save(configFile);
        } catch(Exception e) {
            plugin.getLogger().log(Level.SEVERE, ChatColor.DARK_RED + "Could not save config config.yml !", e);
        }
    }
}
