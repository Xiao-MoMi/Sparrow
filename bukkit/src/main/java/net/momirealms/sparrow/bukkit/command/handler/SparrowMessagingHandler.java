package net.momirealms.sparrow.bukkit.command.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class SparrowMessagingHandler<C extends CommandSender> implements CommandExecutionHandler<C> {

    @NotNull
    @Contract(value = " -> new", pure = true)
    public static <C extends CommandSender> SparrowMessagingHandler<C> instance() {
        return new SparrowMessagingHandler<>();
    }

    @Override
    public void execute(@NonNull CommandContext<C> commandContext) {
        Optional<TranslatableComponent.Builder> optionalMessage = commandContext.optional(SparrowArgumentKeys.MESSAGE);
        if (optionalMessage.isEmpty()) {
            return;
        }

        boolean silent = commandContext.flags().hasFlag(SparrowFlagKeys.SILENT_FLAG);
        if (silent)
            return;

        List<Component> args = commandContext.optional(SparrowArgumentKeys.MESSAGE_ARGS).orElse(Collections.emptyList());
        TranslatableComponent.Builder message = optionalMessage.get();
        SparrowBukkitPlugin.getInstance().getSenderFactory()
                .wrap(commandContext.sender())
                .sendMessage(
                        TranslationManager.render(message.arguments(args).build()),
                        true
                );

    }
}
