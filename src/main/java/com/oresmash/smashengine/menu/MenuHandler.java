package com.oresmash.smashengine.menu;

import com.oresmash.smashengine.SmashEngine;
import com.oresmash.smashengine.item.ItemBuilder;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static com.oresmash.smashengine.SmashEngine.textUtils;

public abstract class MenuHandler implements InventoryHolder {
    private final Inventory inventory;
    @Getter
    private final int rows;
    @Getter
    private final String title;
    private final Player player;

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

    public MenuHandler(Player player, ConfigurationSection configSection) {
        this(
                player,
                configSection.getString("title", "Menu"),
                configSection.getInt("rows", 6)
        );
        setupFromConfig(configSection);
    }

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

    public abstract void onClick(InventoryClickEvent event);

    public abstract void setContents();

    public void open(Player player) {
        setContents();
        player.openInventory(this.inventory);
    }

    public void update() {
        player.updateInventory();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    protected ItemStack editItem(ItemStack item, int slot, ConfigurationSection itemSection) {
        return item;
    }

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

    public void fill(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
    }

    public void clear() {
        inventory.clear();
    }

    public void showError(int slot, ItemStack originalItem, String message) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.75f);
        ItemStack barrierItem = new ItemBuilder(Material.BARRIER)
                .name("<white>\uE00F<red>" + message)
                .build();
        getInventory().setItem(slot, barrierItem);
        Bukkit.getScheduler().runTaskLater(SmashEngine.getPlugin(SmashEngine.class), () -> {
            getInventory().setItem(slot, originalItem);
            update();
        }, 30L);
    }

    public void onClose(InventoryCloseEvent event) {
        // Override this method to handle actions when the inventory is closed
    }

    protected boolean isClickInTopInventory(InventoryClickEvent event) {
        return event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory());
    }

    protected boolean isClickInBottomInventory(InventoryClickEvent event) {
        return event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getBottomInventory());
    }

}
