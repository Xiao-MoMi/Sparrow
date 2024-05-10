package net.momirealms.sparrow.bukkit.command.key;

import net.kyori.adventure.text.TranslatableComponent;
import org.incendo.cloud.key.CloudKey;

public final class MetaKeys {
    public static final CloudKey<TranslatableComponent.Builder> SUCCESS_SINGLE_MESSAGE = CloudKey.of("success_single_message", TranslatableComponent.Builder.class);
    public static final CloudKey<TranslatableComponent.Builder> SUCCESS_MULTIPLE_MESSAGE = CloudKey.of("success_multiple_message", TranslatableComponent.Builder.class);
}
