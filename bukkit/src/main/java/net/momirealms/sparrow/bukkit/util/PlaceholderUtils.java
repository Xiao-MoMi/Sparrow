package net.momirealms.sparrow.bukkit.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderUtils {

    public static String parse(OfflinePlayer player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static String parse(Player p1, Player p2, String text) {
        return PlaceholderAPI.setRelationalPlaceholders(p1, p2, text);
    }
}
