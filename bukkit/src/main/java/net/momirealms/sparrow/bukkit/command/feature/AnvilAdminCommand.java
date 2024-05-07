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

public class AnvilAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "anvil_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .flag(manager.flagBuilder("title").withAliases("t").withComponent(StringParser.greedyFlagYieldingStringParser()))
                .flag(manager.flagBuilder("legacy-color").withAliases("l"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    var players = selector.values();
                    boolean silent = commandContext.flags().hasFlag("silent");
                    boolean legacy = commandContext.flags().hasFlag("legacy-color");
                    boolean customTitle = commandContext.flags().hasFlag("title");
                    for (Player player : players) {
                        player.openAnvil(null, true);
                        if (customTitle) {
                            String containerTitle = (String) commandContext.flags().getValue("title").get();
                            String json = AdventureHelper.componentToJson(AdventureHelper.getMiniMessage().deserialize(
                                    legacy ? AdventureHelper.legacyToMiniMessage(containerTitle) : containerTitle
                            ));
                            SparrowNMSProxy.getInstance().updateInventoryTitle(
                                    player, json
                            );
                        }
                    }
                    if (!silent) {
                        if (players.size() == 1) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_ANVIL_SUCCESS_SINGLE
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
                                                    MessageConstants.COMMANDS_ADMIN_ANVIL_SUCCESS_MULTIPLE
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
