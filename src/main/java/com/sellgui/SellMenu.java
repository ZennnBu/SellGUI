
package com.sellgui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SellMenu {

    private final SellGUI plugin;

    public SellMenu(SellGUI plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "§8SellGUI");

        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", List.of());

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        double total = getInventoryValue(player);

        inventory.setItem(13, createItem(
                Material.GOLD_INGOT,
                "§6Inventory Sell Value",
                List.of("§7Your items are worth:", "§a" + plugin.getEconomyManager().format(total))
        ));

        inventory.setItem(11, createItem(
                Material.EMERALD,
                "§aSell All Items",
                List.of("§7Sell all eligible items", "§eClick to sell your inventory")
        ));

        inventory.setItem(15, createItem(
                Material.BARRIER,
                "§cClose",
                List.of("§7Click to close this menu")
        ));

        player.openInventory(inventory);
    }

    public double getInventoryValue(Player player) {
        double total = 0.0;

        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType().isAir()) continue;

            double price = plugin.getItemPrice(item.getType());
            total += price * item.getAmount();
        }

        return total * plugin.getConfig().getDouble("settings.sell-multiplier", 1.0);
    }

    public boolean sellAll(Player player) {
        double multiplier = plugin.getConfig().getDouble("settings.sell-multiplier", 1.0);
        double total = 0.0;

        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType().isAir()) continue;

            double price = plugin.getItemPrice(item.getType());
            if (price > 0.0) {
                total += price * item.getAmount();
            }
        }

        total *= multiplier;

        if (total <= 0.0) {
            player.sendMessage("§cYou don't have any sellable items!");
            return false;
        }

        if (!plugin.getEconomyManager().deposit(player, total)) {
            player.sendMessage("§cPayment failed. Your items were not removed.");
            return false;
        }

        for (int slot = 0; slot < player.getInventory().getStorageContents().length; slot++) {
            ItemStack item = player.getInventory().getItem(slot);

            if (item == null || item.getType().isAir()) continue;

            if (plugin.getItemPrice(item.getType()) > 0.0) {
                player.getInventory().setItem(slot, null);
            }
        }

        player.sendMessage("§aSold your items for §e"
                + plugin.getEconomyManager().format(total) + "§a!");
        return true;
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(new ArrayList<>(lore));
            item.setItemMeta(meta);
        }

        return item;
    }
}
