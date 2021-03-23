package adminshop.listeners;

import adminshop.Main;
import adminshop.commands.shop.Shop;
import adminshop.utils.ShopUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class EventListener implements Listener {

    private Main plugin;
    private ShopUtils utils;

    public EventListener(Main plugin) {
        this.plugin = plugin;
        this.utils = new ShopUtils(plugin);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player)event.getWhoClicked();
        ItemStack current = event.getCurrentItem();

        if(current == null)
            return;

        if(plugin.inShop.get(player.getUniqueId())) {

            event.setCancelled(true);

            if(plugin.shopStep.get(player.getUniqueId()) == 1) {
                for(HashMap.Entry<Integer, ArrayList<String>> entry: utils.getCategories().entrySet()) {
                    ArrayList<String> category = entry.getValue();

                    if(current.getType() == Material.valueOf(category.get(1))) {
                        plugin.shopStep.put(player.getUniqueId(), 2);
                        player.closeInventory();

                        utils.openShop(player, 2, entry.getKey(), plugin.shopPage.get(player.getUniqueId()), null);
                    }
                }
            }
            else if(plugin.shopStep.get(player.getUniqueId()) == 2) {
                if(current.getType() == Material.WORKBENCH) {
                    // Retour aux catégories
                    plugin.shopStep.put(player.getUniqueId(), 1);
                    player.closeInventory();

                    utils.openShop(player, 1, 0, plugin.shopPage.get(player.getUniqueId()), null);
                }
                else if(current.getType() == Material.ARROW) {
                    Integer page = plugin.shopPage.get(player.getUniqueId());

                    if(current.getItemMeta().getDisplayName().equals("Page précédente")) {
                        // Page précédente
                        plugin.shopStep.put(player.getUniqueId(), 2);
                        player.closeInventory();

                        page--;

                        if(page <= 1)
                            page = 1;

                        utils.openShop(player, 2, plugin.shopCategory.get(player.getUniqueId()), page, null);
                    }
                    if(current.getItemMeta().getDisplayName().equals("Page suivante")) {
                        // Page suivante
                        plugin.shopStep.put(player.getUniqueId(), 2);
                        player.closeInventory();

                        page++;

                        if(page >= utils.getCountItemsPages(plugin.shopCategory.get(player.getUniqueId())))
                            page = utils.getCountItemsPages(plugin.shopCategory.get(player.getUniqueId()));

                        utils.openShop(player, 2, plugin.shopCategory.get(player.getUniqueId()), page, null);
                    }
                }
                else {
                    for(HashMap.Entry<Integer, ArrayList<String>> entry: utils.getItems(player, plugin.shopCategory.get(player.getUniqueId())).entrySet()) {
                        ArrayList<String> item = entry.getValue();

                        if(current.getType() == Material.valueOf(item.get(1))) {
                            plugin.shopStep.put(player.getUniqueId(), 3);
                            player.closeInventory();

                            utils.openShop(player, 3, plugin.shopCategory.get(player.getUniqueId()), plugin.shopPage.get(player.getUniqueId()), entry.getKey());
                        }
                    }
                }
            }
            else if(plugin.shopStep.get(player.getUniqueId()) == 3) {
                if(current.getType() == Material.WORKBENCH) {
                    // Retour aux pages
                    plugin.shopStep.put(player.getUniqueId(), 2);
                    player.closeInventory();

                    utils.openShop(player, 2, plugin.shopCategory.get(player.getUniqueId()), plugin.shopPage.get(player.getUniqueId()), null);
                }
                else {
                    Integer slot = event.getSlot();

                    // ACHATS
                    if(slot >= 11 && slot <= 15) {
                        Double balance = plugin.econ.getBalance(player);
                        Integer quantity = 0;

                        switch(slot) {
                            case 11:
                                quantity = 1;
                                break;
                            case 12:
                                quantity = 8;
                                break;
                            case 13:
                                quantity = 16;
                                break;
                            case 14:
                                quantity = 32;
                                break;
                            case 15:
                                quantity = 64;
                                break;
                        }

                        Double amount = quantity * utils.getItemBuyPrice(plugin.shopCategory.get(player.getUniqueId()), plugin.shopItem.get(player.getUniqueId()));

                        if(balance < amount) {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 15, 1);
                        }
                        else {
                            EconomyResponse r = plugin.econ.withdrawPlayer(player, amount);
                            if(r.transactionSuccess()) {
                                ItemStack itemStack = new ItemStack(Material.valueOf(utils.getItemMaterial(plugin.shopCategory.get(player.getUniqueId()), plugin.shopItem.get(player.getUniqueId()))));
                                itemStack.setAmount(quantity);

                                player.getInventory().addItem(itemStack);

                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 15, 1);
                            }
                            else {
                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 15, 1);
                            }
                        }
                    }
                    if(slot >= 20 && slot <= 24) {
                        Integer quantity = 0;

                        switch(slot) {
                            case 20:
                                quantity = 1;
                                break;
                            case 21:
                                quantity = 8;
                                break;
                            case 22:
                                quantity = 16;
                                break;
                            case 23:
                                quantity = 32;
                                break;
                            case 24:
                                quantity = 64;
                                break;
                        }

                        Double amount = quantity * utils.getItemSellPrice(plugin.shopCategory.get(player.getUniqueId()), plugin.shopItem.get(player.getUniqueId()));
                        ItemStack itemStack = new ItemStack(Material.valueOf(utils.getItemMaterial(plugin.shopCategory.get(player.getUniqueId()), plugin.shopItem.get(player.getUniqueId()))));

                        if(!player.getInventory().containsAtLeast(itemStack, quantity)) {
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 15, 1);
                        }
                        else {
                            EconomyResponse r = plugin.econ.depositPlayer(player, amount);
                            if(r.transactionSuccess()) {
                                itemStack.setAmount(quantity);

                                player.getInventory().removeItem(itemStack);

                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 15, 1);
                            }
                            else {
                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 15, 1);
                            }
                        }
                    }
                    //player.setItem
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = (Player)event.getPlayer();

        plugin.inShop.put(player.getUniqueId(), false);
    }

    @EventHandler
    public void onPlayerQuitInventory(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();

        if(plugin.inShop.get(player.getUniqueId()))
            plugin.inShop.put(player.getUniqueId(), false);
    }
}
