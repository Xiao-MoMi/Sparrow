package net.momirealms.sparrow.common.command.key;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.incendo.cloud.key.CloudKey;

import java.util.List;

public final class SparrowArgumentKeys {
    public static final CloudKey<Long> TIME = CloudKey.of("time", Long.class);
    public static final CloudKey<TranslatableComponent.Builder> MESSAGE = CloudKey.of("message", TranslatableComponent.Builder.class);
    /**
     * The key for the message arguments.
     * <p>
     * <b>Note!</b> The component list should be <b>immutable</b>!
     */
    public static final CloudKey<List<Component>> MESSAGE_ARGS = CloudKey.of("message_args", new TypeToken<>() {
    });
}
