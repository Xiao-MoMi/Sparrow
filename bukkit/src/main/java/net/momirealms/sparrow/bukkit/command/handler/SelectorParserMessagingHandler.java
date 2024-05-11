package net.momirealms.sparrow.bukkit.command.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.meta.CommandMeta;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class SelectorParserMessagingHandler<S extends Selector<?>> implements CommandExecutionHandler<CommandSender> {
    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        boolean isCall = commandContext.optional(SparrowArgumentKeys.IS_CALLBACK).orElse(false);
        if (!isCall) {
            return;
        }

        boolean silent = commandContext.flags().hasFlag(SparrowFlagKeys.SILENT_FLAG);
        if (silent)
            return;

        Optional<S> selector = this.parseSelector(commandContext);
        if (selector.isEmpty())
            return;

        CommandMeta meta = commandContext.command().commandMeta();
        List<Component> args = commandContext.optional(SparrowArgumentKeys.MESSAGE_ARGS).orElse(Collections.emptyList());
        if (selector.get().values().size() == 1) {
            meta.optional(SparrowMetaKeys.SELECTOR_SUCCESS_SINGLE_MESSAGE).ifPresent(message -> this.sendSingleMessage(commandContext, message, args, selector.get()));
        } else {
            meta.optional(SparrowMetaKeys.SELECTOR_SUCCESS_MULTIPLE_MESSAGE).ifPresent(message -> this.sendMultipleMessage(commandContext, message, args, selector.get()));
        }
    }

    protected abstract Optional<S> parseSelector(CommandContext<CommandSender> commandContext);
    protected abstract void sendSingleMessage(CommandContext<CommandSender> commandContext, TranslatableComponent.Builder singleMessage, List<Component> args, S selector);
    protected abstract void sendMultipleMessage(CommandContext<CommandSender> commandContext, TranslatableComponent.Builder multipleMessage, List<Component> args, S selector);
}
