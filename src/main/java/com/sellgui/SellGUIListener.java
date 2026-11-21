
package com.sellgui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class SellGUIListener implements Listener {

    private final SellGUI plugin;
    private final SellMenu sellMenu;
    private static final String GUI_TITLE = "§8SellGUI";

    public SellGUIListener(SellGUI plugin, SellMenu sellMenu) {
        this.plugin = plugin;
        this.sellMenu = sellMenu;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        switch (event.getSlot()) {
            case 11 -> {
                sellMenu.sellAll(player);
                sellMenu.open(player);
            }
            case 15 -> player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }
}
