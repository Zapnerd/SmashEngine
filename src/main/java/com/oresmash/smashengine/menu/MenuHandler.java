package com.oresmash.smashengine.menu;

import com.oresmash.smashengine.item.ItemBuilder;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static com.oresmash.smashengine.SmashEngine.textUtils;

/**
 * Abstract class representing a menu handler for creating and managing custom GUIs.
 */
public abstract class MenuHandler implements InventoryHolder {
    private final Inventory inventory;
    @Getter
    private final int rows;
    @Getter
    private final String title;
    private final Player player;

    /**
     * Constructs a MenuHandler with the specified title and rows.
     *
     * @param player The player for whom the menu is being created.
     * @param title  The title of the menu.
     * @param rows   The number of rows in the menu.
     */
    public MenuHandler(Player player, String title, int rows) {
        this.player = player;
        this.title = title;
        if (rows > 6) {
            this.rows = 6;
            Bukkit.getLogger().warning("The GUI " + title + " has more than 6 rows, it will be capped at 6");
        } else {
            this.rows = rows;
        }
        this.inventory = Bukkit.createInventory(this, this.rows * 9, textUtils.colorize(title));
    }

    /**
     * Constructs a MenuHandler from a configuration section.
     *
     * @param player        The player for whom the menu is being created.
     * @param configSection The configuration section.
     */
    public MenuHandler(Player player, ConfigurationSection configSection) {
        this(
                player,
                configSection.getString("title", "Menu"),
                configSection.getInt("rows", 6)
        );
        setupFromConfig(configSection);
    }

    /**
     * Sets up the menu from a configuration section.
     *
     * @param configSection The configuration section.
     */
    private void setupFromConfig(ConfigurationSection configSection) {
        if (configSection.contains("filler")) {
            Material fillerMaterial = Material.getMaterial(configSection.getString("filler"));
            if (fillerMaterial != null) {
                ItemStack fillerItem = new ItemBuilder(fillerMaterial).name(" ").build();
                fill(fillerItem);
            }
        }

        if (configSection.contains("border")) {
            Material borderMaterial = Material.getMaterial(configSection.getString("border"));
            if (borderMaterial != null) {
                ItemStack borderItem = new ItemBuilder(borderMaterial).name(" ").build();
                border(borderItem);
            }
        }

        ConfigurationSection contentsSection = configSection.getConfigurationSection("contents");
        if (contentsSection != null) {
            for (String key : contentsSection.getKeys(false)) {
                ConfigurationSection itemSection = contentsSection.getConfigurationSection(key);
                if (itemSection == null) continue;

                int slot = Integer.parseInt(key);
                Material material = Material.getMaterial(itemSection.getString("material", "PAPER"));
                if (material == null) continue;

                String name = itemSection.getString("display", "Unnamed Item");
                List<String> lore = itemSection.getStringList("lore");

                // PlaceholderAPI replacements
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    name = PlaceholderAPI.setPlaceholders(player, name);
                    lore = lore.stream().map(line -> PlaceholderAPI.setPlaceholders(player, line)).collect(Collectors.toList());
                }

                ItemBuilder itemBuilder = new ItemBuilder(material)
                        .name(name)
                        .lore(lore)
                        .glow(itemSection.getBoolean("glow", false))
                        .hideflags(itemSection.getBoolean("hideflags", false));

                if (itemSection.contains("texture")) {
                    itemBuilder.texture(itemSection.getString("texture"));
                }

                ItemStack itemStack = itemBuilder.build();
                itemStack = editItem(itemStack, slot, itemSection);
                getInventory().setItem(slot, itemStack);
            }
        }
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
        setContents();
        player.openInventory(this.inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Method to customize items before they are set in the inventory.
     *
     * @param item        The item stack to edit.
     * @param slot        The slot in which the item will be placed.
     * @param itemSection The configuration section of the item.
     * @return The edited item stack.
     */
    protected ItemStack editItem(ItemStack item, int slot, ConfigurationSection itemSection) {
        // Override this method to customize item before setting it
        return item;
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
