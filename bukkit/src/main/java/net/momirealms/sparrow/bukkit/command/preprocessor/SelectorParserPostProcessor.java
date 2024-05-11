package net.momirealms.sparrow.bukkit.command.preprocessor;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.services.type.ConsumerService;

import java.util.Optional;

public abstract class SelectorParserPostProcessor<S extends Selector<?>> implements CommandPostprocessor<CommandSender> {

    @Override
    public void accept(@NonNull CommandPostprocessingContext<CommandSender> commandSenderCommandPostprocessingContext) {
        CommandContext<@NonNull CommandSender> context = commandSenderCommandPostprocessingContext.commandContext();
        boolean silent = context.flags().hasFlag(SparrowFlagKeys.SILENT);
        Optional<S> selector = parseSelector(commandSenderCommandPostprocessingContext);
        if (selector.isEmpty()) {
            return;
        }

        var players = selector.get().values();
        if (players.isEmpty()) {
            if (!silent) {
                SparrowBukkitPlugin.getInstance().getSenderFactory()
                        .wrap(context.sender())
                        .sendMessage(
                                TranslationManager.render(
                                        MessageConstants.ARGUMENT_ENTITY_NOTFOUND_PLAYER.build()
                                ),
                                true
                        );
            }
            ConsumerService.interrupt();
        }
    }

    protected abstract Optional<S> parseSelector(CommandPostprocessingContext<CommandSender> commandSenderCommandPostprocessingContext);
}
