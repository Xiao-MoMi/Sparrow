package net.momirealms.sparrow.bukkit.command.key;

import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.key.CloudKey;

public class SparrowBukkitArgumentKeys {
    public static final CloudKey<MultiplePlayerSelector> PLAYER_SELECTOR = CloudKey.of("player_selector", MultiplePlayerSelector.class);
    public static final CloudKey<MultipleEntitySelector> ENTITY_SELECTOR = CloudKey.of("entity_selector", MultipleEntitySelector.class);
}
