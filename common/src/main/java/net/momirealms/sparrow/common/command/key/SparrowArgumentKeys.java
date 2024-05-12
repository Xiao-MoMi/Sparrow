package net.momirealms.sparrow.common.command.key;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.incendo.cloud.key.CloudKey;

import java.util.List;

public final class SparrowArgumentKeys {
    public static final CloudKey<Long> TIME = CloudKey.of("time", Long.class);
}
