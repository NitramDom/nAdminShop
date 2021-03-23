package adminshop.utils;

import adminshop.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.*;
import java.util.logging.Level;

public class ShopUtils {

    private Main plugin;

    public ShopUtils(Main plugin) {
        this.plugin = plugin;
    }

    public TreeMap<Integer, ArrayList<String>> getItems(Player player, Integer categoryId) {
        TreeMap<Integer, ArrayList<String>> items = new TreeMap<Integer, ArrayList<String>>();

        String name;
        String material;
        Double buyPrice;
        Double sellPrice;

        Integer i = 0;
        for(String item: plugin.getItemsConfig().getConfig().getConfigurationSection("categories." + String.valueOf(categoryId) + ".items").getKeys(false)) {
            ArrayList list = new ArrayList();
            Integer page = plugin.shopPage.get(player.getUniqueId());
            Boolean add = false;

            name = plugin.getItemsConfig().getConfig().getString("categories." + categoryId + ".items." + item + ".name");
            material = plugin.getItemsConfig().getConfig().getString("categories." + categoryId + ".items." + item + ".material");
            buyPrice = plugin.getItemsConfig().getConfig().getDouble("categories." + categoryId + ".items." + item + ".buyPrice");
            sellPrice = plugin.getItemsConfig().getConfig().getDouble("categories." + categoryId + ".items." + item + ".sellPrice");

            if(page <= 1 && i < 45)
                add = true;
            if(page >= 2 && (i > ((page - 1) * 44)) && i < ((page * 44)))
                add = true;

            // i est supérieur à 44 et i est inférriieur à 88

            if(add) {
                list.add(name);
                list.add(material);
                list.add(buyPrice);
                list.add(sellPrice);

                items.put(Integer.parseInt(item), list);
            }

            i++;
        }

        return items;
    }

    public HashMap<Integer, ArrayList<String>> getCategories() {
        HashMap<Integer, ArrayList<String>> categories = new HashMap<Integer, ArrayList<String>>();

        String name;
        String material;

        for(String line: plugin.getItemsConfig().getConfig().getConfigurationSection("categories").getKeys(false)) {
            ArrayList<String> list = new ArrayList<String>();

            name = plugin.getItemsConfig().getConfig().getString("categories." + line + ".name");
            material = plugin.getItemsConfig().getConfig().getString("categories." + line + ".material");

            list.add(name);
            list.add(material);

            categories.put(Integer.parseInt(line), list);
        }

        return categories;
    }

    public String getItemName(Integer categoryId, Integer itemId) {
        return plugin.getItemsConfig().getConfig().getString("categories." + String.valueOf(categoryId) + ".items." + String.valueOf(itemId) + ".name");
    }

    public String getItemMaterial(Integer categoryId, Integer itemId) {
        return plugin.getItemsConfig().getConfig().getString("categories." + String.valueOf(categoryId) + ".items." + String.valueOf(itemId) + ".material");
    }

    public Double getItemBuyPrice(Integer categoryId, Integer itemId) {
        return plugin.getItemsConfig().getConfig().getDouble("categories." + String.valueOf(categoryId) + ".items." + String.valueOf(itemId) + ".buyPrice");
    }

    public Double getItemSellPrice(Integer categoryId, Integer itemId) {
        return plugin.getItemsConfig().getConfig().getDouble("categories." + String.valueOf(categoryId) + ".items." + String.valueOf(itemId) + ".sellPrice");
    }

    public String getCategoryName(Integer categoryId) {
        return plugin.getItemsConfig().getConfig().getString("categories." + String.valueOf(categoryId) + ".name");
    }

    public Integer getCountItemsPages(Integer categoryId) {
        Integer count = 1;
        Integer i = 0;

        for(String item: plugin.getItemsConfig().getConfig().getConfigurationSection("categories." + String.valueOf(categoryId) + ".items").getKeys(false)) {
            if(i == 45) {
                count++;
                i = 0;
            }
            else {
                i++;
            }
        }

        return count;
    }

    public Integer getCountCategories() {
        Integer count = 0;

        for(String itemLine: plugin.getItemsConfig().getConfig().getConfigurationSection("categories").getKeys(false)) {
            count++;
        }

        return count;
    }

    public ItemStack getItem(String name, Material material, String lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItem(String name, Material material, String lore1, String lore2) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore1, lore2));

        item.setItemMeta(meta);
        return item;
    }

    public void openShop(Player player, Integer step, Integer categoryId, Integer page, Integer itemId) {
        Inventory shop = null;

        plugin.inShop.put(player.getUniqueId(), true);
        plugin.shopPage.put(player.getUniqueId(), page);

        if(step == 1) {
            if(plugin.shopStep.get(player.getUniqueId()) == 1) {
                shop = Bukkit.createInventory(null, 54, "§8Sélectionnez une catégorie");

                Integer i = 1;
                for(HashMap.Entry<Integer, ArrayList<String>> entry: getCategories().entrySet()) {
                    ArrayList<String> category = entry.getValue();

                    if(i % 2 != 0) {
                        i++;
                    }

                    try {
                        shop.setItem((i - 1), getItem(category.get(0), Material.valueOf(category.get(1)), ChatColor.GREEN + "Cliquez pour accéder à la catégorie"));
                    } catch(Exception e) {
                        plugin.getLogger().log(Level.WARNING, e.getMessage());
                    }
                    i++;
                }
            }
        }
        else if(step == 2) {
            plugin.shopCategory.put(player.getUniqueId(), categoryId);

            shop = Bukkit.createInventory(null, 54, "§8" + getCategoryName(categoryId) + " - " + plugin.shopPage.get(player.getUniqueId()) + "/" + getCountItemsPages(categoryId));

            shop.setItem(45, getItem("Page précédente", Material.ARROW, ""));
            shop.setItem(49, getItem("Retour aux catégories", Material.WORKBENCH, ""));
            shop.setItem(53, getItem("Page suivante", Material.ARROW, ""));

            Integer i = 0;
            for(HashMap.Entry<Integer, ArrayList<String>> entry: getItems(player, categoryId).entrySet()) {
                ArrayList item = entry.getValue();

                try {
                    shop.setItem(i, getItem((String)item.get(0), Material.valueOf((String)item.get(1)), ChatColor.GOLD + "Achat : " + plugin.econ.format((Double)item.get(2)), ChatColor.RED + "Vente : " + plugin.econ.format((Double)item.get(3))));
                } catch(Exception e) {
                    plugin.getLogger().log(Level.WARNING, e.getMessage());
                }
                i++;
            }
        }
        else if(step == 3) {
            plugin.shopItem.put(player.getUniqueId(), itemId);

            String itemName = getItemName(categoryId, itemId);

            shop = Bukkit.createInventory(null, 54, "§8" + itemName + " - Achat/Vente");

            shop.setItem(11, getItem(itemName + " x1", Material.GOLD_BLOCK, ChatColor.GREEN + "Achat : 1 unité", ChatColor.GOLD + "Total : " + plugin.econ.format(1 * getItemBuyPrice(categoryId, itemId))));
            shop.setItem(12, getItem(itemName + " x8", Material.GOLD_BLOCK, ChatColor.GREEN + "Achat : 8 unités", ChatColor.GOLD + "Total : " + plugin.econ.format(8 * getItemBuyPrice(categoryId, itemId))));
            shop.setItem(13, getItem(itemName + " x16", Material.GOLD_BLOCK, ChatColor.GREEN + "Achat : 16 unités", ChatColor.GOLD + "Total : " + plugin.econ.format(16* getItemBuyPrice(categoryId, itemId))));
            shop.setItem(14, getItem(itemName + " x32", Material.GOLD_BLOCK, ChatColor.GREEN + "Achat : 32 unités", ChatColor.GOLD + "Total : " + plugin.econ.format(32 * getItemBuyPrice(categoryId, itemId))));
            shop.setItem(15, getItem(itemName + " x64", Material.GOLD_BLOCK, ChatColor.GREEN + "Achat : 64 unités", ChatColor.GOLD + "Total : " + plugin.econ.format(64 * getItemBuyPrice(categoryId, itemId))));

            shop.setItem(20, getItem(itemName + " x1", Material.REDSTONE_BLOCK, ChatColor.GREEN + "Vente : 1 unité", ChatColor.RED + "Total : " + plugin.econ.format(1 * getItemSellPrice(categoryId, itemId))));
            shop.setItem(21, getItem(itemName + " x8", Material.REDSTONE_BLOCK, ChatColor.GREEN + "Vente : 8 unités", ChatColor.RED + "Total : " + plugin.econ.format(8 * getItemSellPrice(categoryId, itemId))));
            shop.setItem(22, getItem(itemName + " x16", Material.REDSTONE_BLOCK, ChatColor.GREEN + "Vente : 16 unités", ChatColor.RED + "Total : " + plugin.econ.format(16* getItemSellPrice(categoryId, itemId))));
            shop.setItem(23, getItem(itemName + " x32", Material.REDSTONE_BLOCK, ChatColor.GREEN + "Vente : 32 unités", ChatColor.RED + "Total : " + plugin.econ.format(32 * getItemSellPrice(categoryId, itemId))));
            shop.setItem(24, getItem(itemName + " x64", Material.REDSTONE_BLOCK, ChatColor.GREEN + "Vente : 64 unités", ChatColor.RED + "Total : " + plugin.econ.format(64 * getItemSellPrice(categoryId, itemId))));

            shop.setItem(49, getItem("Retour aux pages", Material.WORKBENCH, ""));


        }

        player.openInventory(shop);
    }
}
