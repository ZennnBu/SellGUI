
package com.sellgui;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EconomyManager {

    private final SellGUI plugin;
    private Economy economy;

    public EconomyManager(SellGUI plugin) {
        this.plugin = plugin;
    }

    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault was not found!");
            return false;
        }

        var registration = Bukkit.getServicesManager()
                .getRegistration(Economy.class);

        if (registration == null) {
            plugin.getLogger().warning("No economy provider found!");
            return false;
        }

        economy = registration.getProvider();
        return economy != null;
    }

    public boolean isReady() {
        return economy != null;
    }

    public double getBalance(Player player) {
        if (!isReady()) return 0.0;
        return economy.getBalance(player);
    }

    public boolean deposit(Player player, double amount) {
        if (!isReady() || amount <= 0) return false;

        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public String format(double amount) {
        if (!isReady()) return String.format("%.2f", amount);
        return economy.format(amount);
    }
}
