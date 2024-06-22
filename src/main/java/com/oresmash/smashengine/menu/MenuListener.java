package com.oresmash.smashengine.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    /**
     * Handles the click event for the inventory.
     *
     * @param event The event to handle.
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof MenuHandler)) return;
        event.setCancelled(true);
        MenuHandler menuHandler = (MenuHandler) holder;
        menuHandler.playClickSound((Player) event.getWhoClicked());
        menuHandler.onClick(event);
    }
}
