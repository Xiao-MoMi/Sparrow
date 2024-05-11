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
import java.util.function.Function;

public final class CommandUtils {
    private CommandUtils() {
    }

    public static <C> void storeEntitySelectorMessage(
            CommandContext<C> context,
            @NotNull Selector<? extends Entity> selector,
            Pair<TranslatableComponent.Builder, TranslatableComponent.Builder> message
    ) {
        storeSelectorMessage(context, selector, message, Pair.of(
                s -> List.of(Component.text(s.values().iterator().next().getName())),
                s -> List.of(Component.text(s.values().size()))
        ));
    }

    public static <C, S extends Selector<?>> void storeSelectorMessage(
            CommandContext<C> context,
            @NotNull S selector,
            Pair<TranslatableComponent.Builder, TranslatableComponent.Builder> message,
            Pair<Function<S, List<Component>>, Function<S, List<Component>>> argumentMapper
    ) {
        Collection<?> values = selector.values();
        if (values.isEmpty()) {
            throw new UnsupportedOperationException("Cannot store message for empty selector values.");
        }

        if (values.size() == 1) {
            context.store(SparrowArgumentKeys.MESSAGE, message.left());
            context.store(SparrowArgumentKeys.MESSAGE_ARGS, argumentMapper.left().apply(selector));
        } else {
            context.store(SparrowArgumentKeys.MESSAGE, message.right());
            context.store(SparrowArgumentKeys.MESSAGE_ARGS, argumentMapper.right().apply(selector));
        }
    }
}
