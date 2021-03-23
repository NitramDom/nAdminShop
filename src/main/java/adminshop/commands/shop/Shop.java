package adminshop.commands.shop;

import adminshop.Main;
import adminshop.commands.CommandInterface;
import adminshop.utils.ShopUtils;
import com.ticxo.modelengine.api.ModelEngineAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class Shop implements CommandInterface {

    private Main plugin;
    private ShopUtils utils;

    public Shop(Main plugin) {
        this.plugin = plugin;
        this.utils = new ShopUtils(this.plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        Player player = (Player)sender;



        if(plugin.inShop.containsKey(player.getUniqueId()) && plugin.inShop.get(player.getUniqueId())) {
            player.sendMessage(plugin.prefixError + "Vous êtes déjà dans le shop.");
            return false;
        }

        plugin.inShop.put(player.getUniqueId(), true);
        plugin.shopStep.put(player.getUniqueId(), 1);
        plugin.shopPage.put(player.getUniqueId(), 1);
        plugin.shopCategory.put(player.getUniqueId(), null);
        plugin.shopItem.put(player.getUniqueId(), null);

        utils.openShop(player, 1, 0, plugin.shopPage.get(player.getUniqueId()), null);
        return true;
    }
}
