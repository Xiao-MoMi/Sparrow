package net.momirealms.sparrow.bukkit.util;

import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.util.Pair;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class CommandUtils {
    private CommandUtils() {
    }

    public static <C, S extends Selector<?>> void storeSelectorMessage(CommandContext<C> context, @NotNull S selector, Pair<TranslatableComponent.Builder, TranslatableComponent.Builder> message) {
        Collection<?> values = selector.values();
        if (values.isEmpty()) {
            throw new UnsupportedOperationException("Cannot store message for empty selector values.");
        }

        if (values.size() == 1) {
            context.store(SparrowArgumentKeys.MESSAGE, message.left());
        } else {
            context.store(SparrowArgumentKeys.MESSAGE, message.right());
        }
    }
}
