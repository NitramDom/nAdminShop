package adminshop;

import adminshop.commands.CommandHandler;
import adminshop.commands.shop.Shop;
import adminshop.config.Config;
import adminshop.config.Items;
import adminshop.listeners.EventListener;
import adminshop.utils.ShopUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private final Items items = new Items(this);
    private final Config config = new Config(this);
    private ShopUtils shopUtils = new ShopUtils(this);
    public HashMap<UUID, Boolean> inShop = new HashMap<UUID, Boolean>();
    public HashMap<UUID, Integer> shopStep = new HashMap<UUID, Integer>();
    public HashMap<UUID, Integer> shopPage = new HashMap<UUID, Integer>();
    public HashMap<UUID, Integer> shopCategory = new HashMap<UUID, Integer>();
    public HashMap<UUID, Integer> shopItem = new HashMap<UUID, Integer>();
    public Economy econ = null;
    private Integer maxCategories = 24;
    public static String prefixError = ChatColor.GRAY + "[" + ChatColor.RED + "%pluginname%" + ChatColor.GRAY + "] "
            + ChatColor.GRAY;
    public static String prefixPlugin = ChatColor.GRAY + "[" + ChatColor.GOLD + "%pluginname%" + ChatColor.GRAY + "] "
            + ChatColor.GRAY;

    @Override
    public void onEnable() {
        super.onEnable();

        if (!setupEconomy() ) {
            getLogger().log(Level.SEVERE, "Disabled due to no Vault dependency found !");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        loadConfigurationFiles();
        loadCommands();

        Integer countCategories = shopUtils.getCountCategories();

        if(countCategories > maxCategories) {
            getLogger().log(Level.WARNING, "Le nombre maximum de catégories est de : " + maxCategories + ".");
            getPluginLoader().disablePlugin(this);
        } else {
            getServer().getConsoleSender().sendMessage(prefixPlugin + ChatColor.GOLD + "Chargement de " + countCategories + " catégories.");
        }

        getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void loadCommands() {
        CommandHandler handler = new CommandHandler();

        handler.register("openShop", new Shop(this));
        getCommand("shop").setExecutor(handler);
    }

    public void loadConfigurationFiles() {
        FileConfiguration itemsCfg = getItemsConfig().getConfig();
        FileConfiguration defaultCfg = getDefaultConfig().getConfig();

        if(!itemsCfg.isSet("categories"))
            itemsCfg.set("categories", "");

        itemsCfg.options().copyDefaults(true);
        getItemsConfig().save();

        if(!defaultCfg.isSet("pluginName"))
            defaultCfg.set("pluginName", "Nitram Admin Shop");

        defaultCfg.options().copyDefaults(true);
        getDefaultConfig().save();

        getItemsConfig().reloadConfig();
        getDefaultConfig().reloadConfig();

        prefixError = prefixError.replace("%pluginname%", defaultCfg.getString("pluginName"));
        prefixPlugin = prefixPlugin.replace("%pluginname%", defaultCfg.getString("pluginName"));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Items getItemsConfig() {
        return items;
    }

    public Config getDefaultConfig() {
        return config;
    }

    public Economy getEconomy() {
        return econ;
    }

}
