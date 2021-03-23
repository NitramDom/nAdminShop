package adminshop.config;

import adminshop.Main;
import adminshop.config.Items;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

public class Items {

    private Main plugin;
    private FileConfiguration itemsConfig = null;
    private File itemsFile = null;

    public Items(Main plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig() {
        if(itemsFile == null) {
            itemsFile = new File(plugin.getDataFolder().getPath(), "items.yml");
        }

        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);

        save();

        // Look for defaults in the jar
        try {
            Reader defConfigStream = new FileReader(itemsFile);
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                itemsConfig.setDefaults(defConfig);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if(itemsConfig == null) {
            reloadConfig();
        }
        return itemsConfig;
    }

    public void save() {
        if(itemsConfig == null || itemsFile == null) {
            return;
        }

        try {
            getConfig().save(itemsFile);
        } catch(Exception e) {
            plugin.getLogger().log(Level.SEVERE, ChatColor.DARK_RED + "Could not save config items.yml !", e);
        }
    }
}
