package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.Message;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.StringParser;

public class ActionBarAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "actionbar_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("actionbar", StringParser.greedyFlagYieldingStringParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .flag(manager.flagBuilder("legacy-color").withAliases("l"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    var players = selector.values();
                    String actionBarContent = commandContext.get("actionbar");
                    boolean legacy = commandContext.flags().hasFlag("legacy-color");
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
                        String json = AdventureHelper.componentToJson(AdventureHelper.getMiniMessage().deserialize(
                                legacy ? AdventureHelper.legacyToMiniMessage(actionBarContent) : actionBarContent
                        ));
                        SparrowBukkitPlugin.getInstance().getCoreNMSBridge().getHeart().sendActionBar(
                                player, json
                        );
                    }
                    if (!silent) {
                        if (players.size() == 1) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_ACTIONBAR_SUCCESS_SINGLE
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
                                                    Message.COMMANDS_ADMIN_ACTIONBAR_SUCCESS_MULTIPLE
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
