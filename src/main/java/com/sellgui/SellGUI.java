
package com.sellgui;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SellGUI extends JavaPlugin {

    private Set<Material> disabledItems;
    private EconomyManager economyManager;
    private SellMenu sellMenu;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSellSettings();

        economyManager = new EconomyManager(this);
        economyManager.setupEconomy();

        sellMenu = new SellMenu(this);
        getServer().getPluginManager().registerEvents(
                new SellGUIListener(this, sellMenu), this
        );

        getLogger().info("SellGUI enabled!");
    }

    private void loadSellSettings() {
        disabledItems = getConfig().getStringList("settings.disabled-items")
                .stream()
                .map(Material::matchMaterial)
                .filter(material -> material != null)
                .collect(Collectors.toSet());
    }

    public double getItemPrice(Material material) {
        if (material == null || material.isAir() || disabledItems.contains(material)) {
            return 0.0;
        }

        String name = material.name();

        if (getConfig().contains("prices." + name)) {
            return Math.max(0.0, getConfig().getDouble("prices." + name));
        }

        return Math.max(0.0, getConfig().getDouble("settings.default-price", 1.0));
    }

    public boolean isSellable(Material material) {
        return getItemPrice(material) > 0.0;
    }

    public Set<Material> getSellableMaterials() {
        return Arrays.stream(Material.values())
                .filter(material -> !material.isAir())
                .filter(this::isSellable)
                .collect(Collectors.toSet());
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("sellgui")) return false;

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
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (economyManager == null || !economyManager.isReady()) {
            player.sendMessage("§cVault economy is unavailable!");
            return true;
        }

        sellMenu.open(player);
        return true;
    }
}
