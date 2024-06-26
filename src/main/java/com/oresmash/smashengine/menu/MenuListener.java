package com.oresmash.smashengine.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
        if (!(holder instanceof MenuHandler menuHandler)) return;
        event.setCancelled(true);
        menuHandler.onClick(event);
    }

    /**
     * Handles the close event for the inventory.
     *
     * @param event The event to handle.
     */
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof MenuHandler menuHandler) {
            menuHandler.onClose(event);
        }
    }
}
