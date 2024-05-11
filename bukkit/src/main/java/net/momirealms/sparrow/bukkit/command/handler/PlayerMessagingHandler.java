package net.momirealms.sparrow.bukkit.command.handler;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.meta.CommandMeta;

import java.util.Collections;
import java.util.List;

public final class PlayerMessagingHandler implements CommandExecutionHandler<Player> {
    private static final PlayerMessagingHandler INSTANCE = new PlayerMessagingHandler();

    private PlayerMessagingHandler() {
    }

    public static PlayerMessagingHandler instance() {
        return INSTANCE;
    }

    @Override
    public void execute(@NonNull CommandContext<Player> commandContext) {
        boolean isCall = commandContext.optional(SparrowArgumentKeys.IS_CALLBACK).orElse(false);
        if (!isCall) {
            return;
        }

        boolean silent = commandContext.flags().hasFlag(SparrowFlagKeys.SILENT_FLAG);
        if (silent)
            return;
        CommandMeta meta = commandContext.command().commandMeta();
        List<Component> args = commandContext.optional(SparrowArgumentKeys.MESSAGE_ARGS).orElse(Collections.emptyList());

        meta.optional(SparrowMetaKeys.PLAYER_SUCCESS_MESSAGE).ifPresent(message -> SparrowBukkitPlugin.getInstance().getSenderFactory()
                .wrap(commandContext.sender())
                .sendMessage(
                        TranslationManager.render(message.arguments(args).build()),
                        true
                )
        );
    }
}
