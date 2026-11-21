
package com.sellgui;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SellGUI extends JavaPlugin {

    private Set<Material> disabledItems;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSellSettings();

        getLogger().info("SellGUI enabled successfully!");
        getLogger().info("All-material pricing system loaded.");
    }

    private void loadSellSettings() {
        disabledItems = getConfig().getStringList("settings.disabled-items")
                .stream()
                .map(name -> Material.matchMaterial(name))
                .filter(material -> material != null)
                .collect(Collectors.toSet());
    }

    public double getItemPrice(Material material) {
        if (material == null || material.isAir() || disabledItems.contains(material)) {
            return 0.0;
        }

        String itemName = material.name();

        if (getConfig().contains("prices." + itemName)) {
            return Math.max(0.0, getConfig().getDouble("prices." + itemName));
        }

        return Math.max(0.0, getConfig().getDouble("settings.default-price", 1.0));
    }

    public boolean isSellable(Material material) {
        return getItemPrice(material) > 0.0;
    }

    public Set<Material> getSellableMaterials() {
        return java.util.Arrays.stream(Material.values())
                .filter(material -> !material.isAir())
                .filter(this::isSellable)
                .collect(Collectors.toSet());
    }

    @Override
    public void onDisable() {
        getLogger().info("SellGUI disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("sellgui")) {
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("sellgui.admin")) {
                sender.sendMessage("§cYou don't have permission.");
                return true;
            }

            reloadConfig();
            loadSellSettings();
            sender.sendMessage("§aSellGUI config reloaded!");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        player.sendMessage("§aSellGUI pricing system is ready!");
        player.sendMessage("§7GUI is being developed.");
        return true;
    }
}
