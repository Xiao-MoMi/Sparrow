package net.momirealms.sparrow.bukkit.util;

import org.bukkit.Bukkit;

public final class PluginUtils {
    private PluginUtils() {
    }

    public static boolean isPluginEnabled(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }
}
