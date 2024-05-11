package net.momirealms.sparrow.bukkit.command.preprocessor;

import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;

import java.util.Optional;

public final class MultipleEntitySelectorPostProcessor extends SelectorParserPostProcessor<MultipleEntitySelector> {
    private static final MultipleEntitySelectorPostProcessor INSTANCE = new MultipleEntitySelectorPostProcessor();

    private MultipleEntitySelectorPostProcessor() {
    }

    public static MultipleEntitySelectorPostProcessor instance() {
        return INSTANCE;
    }

    @Override
    protected Optional<MultipleEntitySelector> parseSelector(CommandPostprocessingContext<CommandSender> commandSenderCommandPostprocessingContext) {
        CommandContext<CommandSender> context = commandSenderCommandPostprocessingContext.commandContext();
        return context.optional(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
    }
}
