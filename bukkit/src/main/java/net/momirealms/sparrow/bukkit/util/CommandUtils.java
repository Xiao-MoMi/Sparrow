package net.momirealms.sparrow.bukkit.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.entity.Entity;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class CommandUtils {
    private CommandUtils() {
    }

    public static <C> void storeEntitySelectorMessage(
            CommandContext<C> context,
            @NotNull Selector<? extends Entity> selector,
            Pair<TranslatableComponent.Builder, TranslatableComponent.Builder> message
    ) {
        storeSelectorMessage(context, selector, message, Pair.of(
                () -> List.of(Component.text(selector.values().iterator().next().getName())),
                () -> List.of(Component.text(selector.values().size()))
        ));
    }

    public static <C, S extends Selector<?>> void storeSelectorMessage(
            CommandContext<C> context,
            @NotNull S selector,
            Pair<TranslatableComponent.Builder, TranslatableComponent.Builder> message,
            Pair<Supplier<List<Component>>, Supplier<List<Component>>> argumentMapper
    ) {
        Collection<?> values = selector.values();
        if (values.isEmpty()) {
            throw new UnsupportedOperationException("Cannot store message for empty selector values.");
        }

        if (values.size() == 1) {
            context.store(SparrowArgumentKeys.MESSAGE, message.left());
            context.store(SparrowArgumentKeys.MESSAGE_ARGS, argumentMapper.left().get());
        } else {
            context.store(SparrowArgumentKeys.MESSAGE, message.right());
            context.store(SparrowArgumentKeys.MESSAGE_ARGS, argumentMapper.right().get());
        }
    }
}
