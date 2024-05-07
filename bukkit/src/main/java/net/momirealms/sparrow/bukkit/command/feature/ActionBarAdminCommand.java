package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.StringParser;

public class ActionBarAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "actionbar_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .required("actionbar", StringParser.greedyFlagYieldingStringParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .flag(manager.flagBuilder("legacy-color").withAliases("l"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    var players = selector.values();
                    String actionBarContent = commandContext.get("actionbar");
                    boolean legacy = commandContext.flags().hasFlag("legacy-color");
                    boolean silent = commandContext.flags().hasFlag("silent");
                    for (Player player : players) {
                        String json = AdventureHelper.componentToJson(AdventureHelper.getMiniMessage().deserialize(
                                legacy ? AdventureHelper.legacyToMiniMessage(actionBarContent) : actionBarContent
                        ));
                        SparrowNMSProxy.getInstance().sendActionBar(
                                player, json
                        );
                    }
                    if (!silent) {
                        if (players.size() == 1) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_ACTIONBAR_SUCCESS_SINGLE
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
                                                    MessageConstants.COMMANDS_ADMIN_ACTIONBAR_SUCCESS_MULTIPLE
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
