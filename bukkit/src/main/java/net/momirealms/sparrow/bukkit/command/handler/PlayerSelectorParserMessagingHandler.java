package net.momirealms.sparrow.bukkit.command.handler;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.key.ArgumentKeys;
import net.momirealms.sparrow.bukkit.command.key.FlagKeys;
import net.momirealms.sparrow.bukkit.command.key.MetaKeys;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.meta.CommandMeta;

import java.util.Optional;

public class PlayerSelectorParserMessagingHandler implements CommandExecutionHandler<CommandSender> {
    private static final PlayerSelectorParserMessagingHandler INSTANCE = new PlayerSelectorParserMessagingHandler();

    public static PlayerSelectorParserMessagingHandler instance() {
        return INSTANCE;
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        boolean silent = commandContext.flags().hasFlag(FlagKeys.SILENT);
        Optional<MultiplePlayerSelector> selector = commandContext.optional(ArgumentKeys.PLAYER_SELECTOR);
        if (selector.isEmpty()) {
            return;
        }

        if (silent) {
            return;
        }

        var players = selector.get().values();
        CommandMeta meta = commandContext.command().commandMeta();
        meta.optional(MetaKeys.SUCCESS_SINGLE_MESSAGE).ifPresent(message -> {
            SparrowBukkitPlugin.getInstance().getSenderFactory()
                    .wrap(commandContext.sender())
                    .sendMessage(
                            TranslationManager.render(
                                    message.arguments(Component.text(players.iterator().next().getName()))
                                            .build()
                            ),
                            true
                    );
        });

        meta.optional(MetaKeys.SUCCESS_MULTIPLE_MESSAGE).ifPresent(message -> {
            SparrowBukkitPlugin.getInstance().getSenderFactory()
                    .wrap(commandContext.sender())
                    .sendMessage(
                            TranslationManager.render(
                                    message.arguments(Component.text(players.size()))
                                            .build()
                            ),
                            true
                    );
        });
    }
}
