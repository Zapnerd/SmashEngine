package com.oresmash.smashengine.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.oresmash.smashengine.SmashEngine.textUtils;

/**
 * A builder class for creating and customizing ItemStack objects.
 */
public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    private final List<Component> lore;

    /**
     * Constructs an ItemBuilder for the specified material.
     *
     * @param material The material of the item.
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
        this.lore = new ArrayList<>();
    }

    /**
     * Sets the display name of the item.
     *
     * @param name The display name to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder name(String name) {
        meta.displayName(textUtils.colorize(name));
        return this;
    }

    /**
     * Adds a line of lore to the item.
     *
     * @param line The lore line to add.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder lore(String line) {
        lore.add(textUtils.colorize(line));
        return this;
    }

    /**
     * Adds multiple lines of lore to the item.
     *
     * @param lines The lore lines to add.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder lore(List<String> lines) {
        lines.forEach(line -> lore.add(textUtils.colorize(line)));
        return this;
    }

    /**
     * Sets the amount of the item.
     *
     * @param amount The amount to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Sets whether the item should glow.
     *
     * @param glow Whether the item should glow.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder glow(boolean glow) {
        if (glow) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * Sets whether the item should be unbreakable.
     *
     * @param unbreakable Whether the item should be unbreakable.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    /**
     * Sets the custom model data for the item.
     *
     * @param model The custom model data to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder model(int model) {
        meta.setCustomModelData(model);
        return this;
    }

    /**
     * Sets whether the item should hideflags.
     *
     * @param hideflags Whether the item should glow.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder hideflags(boolean hideflags) {
        if (hideflags) {
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_DYE);
        }
        return this;
    }

    /**
     * Sets the custom texture for a player head item.
     *
     * @param texture The texture to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder texture(String texture) {
        if (item.getType() == Material.PLAYER_HEAD) {
            if (meta instanceof SkullMeta skullMeta) {
                UUID randomUUID = UUID.randomUUID();
                PlayerProfile profile = Bukkit.createProfile(randomUUID, null);
                profile.getProperties().add(new ProfileProperty("textures", texture));
                skullMeta.setPlayerProfile(profile);
            }
        }
        return this;
    }

    /**
     * Sets the owner of the player head item.
     *
     * @param owner The owner to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder owner(OfflinePlayer owner) {
        if (item.getType() == Material.PLAYER_HEAD) {
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(owner);
            }
        }
        return this;
    }

    /**
     * Builds the ItemStack with the specified properties.
     *
     * @return The constructed ItemStack.
     */
    public ItemStack build() {
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
