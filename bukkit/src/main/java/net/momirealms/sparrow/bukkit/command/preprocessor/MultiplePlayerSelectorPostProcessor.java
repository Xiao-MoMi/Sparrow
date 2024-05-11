package net.momirealms.sparrow.bukkit.command.preprocessor;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;

import java.util.Optional;

public final class MultiplePlayerSelectorPostProcessor extends SelectorParserPostProcessor<MultiplePlayerSelector> {
    private static final MultiplePlayerSelectorPostProcessor INSTANCE = new MultiplePlayerSelectorPostProcessor();

    private MultiplePlayerSelectorPostProcessor() {
    }

    public static MultiplePlayerSelectorPostProcessor instance() {
        return INSTANCE;
    }

    @Override
    protected Optional<MultiplePlayerSelector> parseSelector(CommandPostprocessingContext<CommandSender> commandSenderCommandPostprocessingContext) {
        CommandContext<CommandSender> context = commandSenderCommandPostprocessingContext.commandContext();
        return context.optional(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
    }

    @Override
    protected void sendErrorMessage(CommandSender sender) {
        SparrowBukkitPlugin.getInstance().getSenderFactory()
                .wrap(sender)
                .sendMessage(
                        TranslationManager.render(
                                MessageConstants.ARGUMENT_ENTITY_NOTFOUND_PLAYER.build()
                        ),
                        true
                );
    }
}
