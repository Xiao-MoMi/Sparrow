package net.momirealms.sparrow.bukkit.command.processor;

import net.momirealms.sparrow.bukkit.SparrowBukkitCommandManager;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.services.type.ConsumerService;

import java.util.Optional;

public class SelectorPostProcessor implements CommandPostprocessor<CommandSender> {

    private final SparrowBukkitCommandManager commandManager;

    public SelectorPostProcessor(SparrowBukkitCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void accept(@NonNull CommandPostprocessingContext<CommandSender> context) {
        CommandContext<@NonNull CommandSender> commandContext = context.commandContext();
        Optional<MultipleEntitySelector> optionalEntitySelector = commandContext.optional(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
        Optional<MultiplePlayerSelector> optionalPlayerSelector = commandContext.optional(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);

        if (optionalPlayerSelector.isEmpty() && optionalEntitySelector.isEmpty()) {
            return;
        }

        boolean silent = commandContext.flags().hasFlag(SparrowFlagKeys.SILENT_FLAG);

        if (optionalEntitySelector.isPresent()) {
            MultipleEntitySelector multipleEntitySelector = optionalEntitySelector.get();
            var entities = multipleEntitySelector.values();
            boolean allowEntitiesEmpty = context.command().commandMeta().getOrDefault(SparrowMetaKeys.ALLOW_EMPTY_ENTITY_SELECTOR, true);
            if (entities.isEmpty() && !allowEntitiesEmpty) {
                if (!silent) {
                    commandManager.handleCommandFeedback(commandContext.sender(), MessageConstants.ARGUMENT_ENTITY_NOTFOUND_ENTITY);
                }
                ConsumerService.interrupt();
                return;
            }
        }

        if (optionalPlayerSelector.isPresent()) {
            MultiplePlayerSelector multiplePlayerSelector = optionalPlayerSelector.get();
            var players = multiplePlayerSelector.values();
            boolean allowPlayersEmpty = context.command().commandMeta().getOrDefault(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, true);
            if (players.isEmpty() && !allowPlayersEmpty) {
                if (!silent) {
                    commandManager.handleCommandFeedback(commandContext.sender(), MessageConstants.ARGUMENT_ENTITY_NOTFOUND_PLAYER);
                }
                ConsumerService.interrupt();
                return;
            }
        }
    }
}
