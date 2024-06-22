package com.oresmash.smashengine.menu;

import com.oresmash.smashengine.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Sound;
import lombok.Getter;

import static com.oresmash.smashengine.SmashEngine.textUtils;

/**
 * Abstract class representing a menu handler for creating and managing custom GUIs.
 */
public abstract class MenuHandler implements InventoryHolder {
    private final Inventory inventory;
    @Getter private final int rows;
    @Getter private final String title;
    @Getter private final Sound clickSound;
    @Getter private final Sound openSound;

    /**
     * Constructs a MenuHandler with the specified title, rows, click sound, and open sound.
     *
     * @param title      The title of the menu.
     * @param rows       The number of rows in the menu.
     * @param clickSound The sound played when an item is clicked.
     * @param openSound  The sound played when the menu is opened.
     */
    public MenuHandler(String title, int rows, Sound clickSound, Sound openSound) {
        this.title = title;
        if (rows > 6) {
            this.rows = 6;
            Bukkit.getLogger().warning("The GUI " + title + " has more than 6 rows, it will be capped at 6");
        } else {
            this.rows = rows;
        }
        this.inventory = Bukkit.createInventory(this, rows * 9, textUtils.colorize(title));
        this.clickSound = clickSound;
        this.openSound = openSound;
    }

    /**
     * Constructs a MenuHandler with the specified title and rows, using default sounds.
     *
     * @param title The title of the menu.
     * @param rows  The number of rows in the menu.
     */
    public MenuHandler(String title, int rows) {
        this(title, rows, Sound.UI_BUTTON_CLICK, Sound.BLOCK_CHEST_OPEN);
    }

    /**
     * Abstract method to handle inventory click events.
     *
     * @param event The InventoryClickEvent.
     */
    public abstract void onClick(InventoryClickEvent event);

    /**
     * Abstract method to set the contents of the inventory.
     */
    public abstract void setContents();

    /**
     * Opens the menu for the specified player.
     *
     * @param player The player to open the menu for.
     */
    public void open(Player player) {
        player.closeInventory();
        setContents();
        player.openInventory(this.inventory);
        playOpenSound(player);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Plays the click sound for the specified player.
     *
     * @param player The player to play the sound for.
     */
    public void playClickSound(Player player) {
        player.playSound(player.getLocation(), clickSound, 1, 1);
    }

    /**
     * Plays the open sound for the specified player.
     *
     * @param player The player to play the sound for.
     */
    public void playOpenSound(Player player) {
        player.playSound(player.getLocation(), openSound, 1, 1);
    }

    /**
     * Fills the border of the inventory with the specified item.
     *
     * @param item The item to fill the border with.
     */
    public void border(ItemStack item) {
        int size = inventory.getSize();
        int columns = 9;

        for (int i = 0; i < columns; i++) {
            inventory.setItem(i, item);
            inventory.setItem(size - columns + i, item);
        }

        for (int i = columns; i < size - columns; i += columns) {
            inventory.setItem(i, item);
            inventory.setItem(i + columns - 1, item);
        }
    }

    /**
     * Fills the entire inventory with the specified item.
     *
     * @param item The item to fill the inventory with.
     */
    public void fill(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
    }

    /**
     * Clears the entire inventory.
     */
    public void clear() {
        inventory.clear();
    }
}
