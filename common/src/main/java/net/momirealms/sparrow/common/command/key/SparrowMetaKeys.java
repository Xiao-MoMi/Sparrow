package net.momirealms.sparrow.common.command.key;

import org.incendo.cloud.key.CloudKey;

public final class SparrowMetaKeys {
    public static final CloudKey<Boolean> ALLOW_EMPTY_ENTITY_SELECTOR = CloudKey.of("allow_empty_entity_selector", Boolean.class);
    public static final CloudKey<Boolean> ALLOW_EMPTY_PLAYER_SELECTOR = CloudKey.of("allow_empty_player_selector", Boolean.class);
}
