package net.momirealms.sparrow.bukkit.command.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.context.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PlayerSelectorParserMessagingHandler extends SelectorParserMessagingHandler<MultiplePlayerSelector> {
    private static final PlayerSelectorParserMessagingHandler INSTANCE = new PlayerSelectorParserMessagingHandler();

    public static PlayerSelectorParserMessagingHandler instance() {
        return INSTANCE;
    }

    private PlayerSelectorParserMessagingHandler() {
    }

    @Override
    protected Optional<MultiplePlayerSelector> parseSelector(CommandContext<CommandSender> commandContext) {
        return commandContext.optional(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
    }

    @Override
    protected void sendSingleMessage(CommandContext<CommandSender> commandContext, TranslatableComponent.Builder singleMessage, List<Component> args, MultiplePlayerSelector selector) {
        var players = selector.values();
        ArrayList<Component> arguments = new ArrayList<>(args);
        arguments.add(0, Component.text(players.iterator().next().getName()));

        SparrowBukkitPlugin.getInstance().getSenderFactory()
                .wrap(commandContext.sender())
                .sendMessage(
                        TranslationManager.render(singleMessage.arguments(arguments).build()),
                        true
                );
    }

    @Override
    protected void sendMultipleMessage(CommandContext<CommandSender> commandContext, TranslatableComponent.Builder multipleMessage, List<Component> args, MultiplePlayerSelector selector) {
        var players = selector.values();
        ArrayList<Component> arguments = new ArrayList<>(args);
        arguments.add(0, Component.text(players.size()));

        SparrowBukkitPlugin.getInstance().getSenderFactory()
                .wrap(commandContext.sender())
                .sendMessage(
                        TranslationManager.render(multipleMessage.arguments(arguments).build()),
                        true
                );
    }
}
