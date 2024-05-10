package net.momirealms.sparrow.bukkit.command.preprocessor;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.key.ArgumentKeys;
import net.momirealms.sparrow.bukkit.command.key.FlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.services.type.ConsumerService;

import java.util.Optional;

public class PlayerSelectorParserPostProcessor implements CommandPostprocessor<CommandSender> {

    private static final PlayerSelectorParserPostProcessor INSTANCE = new PlayerSelectorParserPostProcessor();

    public static PlayerSelectorParserPostProcessor instance() {
        return INSTANCE;
    }

    @Override
    public void accept(@NonNull CommandPostprocessingContext<CommandSender> commandSenderCommandPostprocessingContext) {
        CommandContext<@NonNull CommandSender> context = commandSenderCommandPostprocessingContext.commandContext();
        boolean silent = context.flags().hasFlag(FlagKeys.SILENT);
        Optional<MultiplePlayerSelector> selector = context.optional(ArgumentKeys.PLAYER_SELECTOR);
        if (selector.isEmpty()) {
            return;
        }

        if (selector.get().values().isEmpty()) {
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
}
