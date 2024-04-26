package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.locale.Message;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;

public class AnvilAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "anvil_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    var players = selector.values();
                    boolean silent = commandContext.flags().hasFlag("silent");
                    if (players.size() == 0) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.ARGUMENT_ENTITY_NOTFOUND_PLAYER.build()
                                            ),
                                            true
                                    );
                        return;
                    }
                    for (Player player : players) {
                        player.openAnvil(null, true);
                    }
                    if (!silent) {
                        if (players.size() == 1) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_ANVIL_SUCCESS_SINGLE
                                                            .arguments(Component.text(players.iterator().next().getName()))
                                                            .build()
                                            ),
                                            true
                                    );
                        } else {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_ANVIL_SUCCESS_MULTIPLE
                                                            .arguments(Component.text(players.size()))
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                    }
                });
    }
}
