package com.oresmash.smashengine;

import com.oresmash.smashengine.menu.MenuListener;
import com.oresmash.smashengine.utils.TextUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class SmashEngine extends JavaPlugin {
    public static TextUtils textUtils;

    @Override
    public void onEnable() {
        textUtils = new TextUtils();
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
    }

}
