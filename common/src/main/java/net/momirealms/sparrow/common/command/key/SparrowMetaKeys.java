package net.momirealms.sparrow.common.command.key;

import net.kyori.adventure.text.TranslatableComponent;
import org.incendo.cloud.key.CloudKey;

public final class SparrowMetaKeys {
    public static final CloudKey<TranslatableComponent.Builder> SELECTOR_SUCCESS_SINGLE_MESSAGE = CloudKey.of("selector_success_single_message", TranslatableComponent.Builder.class);
    public static final CloudKey<TranslatableComponent.Builder> SELECTOR_SUCCESS_MULTIPLE_MESSAGE = CloudKey.of("selector_success_multiple_message", TranslatableComponent.Builder.class);
}
