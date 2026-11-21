
package com.sellgui;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellGUI extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SellGUI has been enabled!");
        getLogger().info("Minecraft 26.2 support initialized.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SellGUI has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("sellgui")) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }

            player.sendMessage("§aSellGUI is working! GUI coming soon.");
            return true;
        }

        return false;
    }
}
