package net.momirealms.sparrow.bukkit.command.preprocessor;

import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
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
}
