package net.momirealms.sparrow.bukkit.command.key;

import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.key.CloudKey;

public final class ArgumentKeys {
    public static final CloudKey<MultiplePlayerSelector> PLAYER_SELECTOR = CloudKey.of("player_selector", MultiplePlayerSelector.class);
}
