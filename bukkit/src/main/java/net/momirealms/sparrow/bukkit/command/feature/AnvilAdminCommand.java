package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.command.handler.PlayerSelectorParserMessagingHandler;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
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
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .flag(manager.flagBuilder("title").withAliases("t").withComponent(StringParser.greedyFlagYieldingStringParser()))
                .flag(SparrowFlagKeys.LEGACY_COLOR_FLAG)
                .meta(SparrowMetaKeys.SELECTOR_SUCCESS_SINGLE_MESSAGE, MessageConstants.COMMANDS_ADMIN_ANVIL_SUCCESS_SINGLE)
                .meta(SparrowMetaKeys.SELECTOR_SUCCESS_MULTIPLE_MESSAGE, MessageConstants.COMMANDS_ADMIN_ANVIL_SUCCESS_MULTIPLE)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    var players = selector.values();
                    boolean legacy = commandContext.flags().hasFlag(SparrowFlagKeys.LEGACY_COLOR_FLAG);
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
                    commandContext.store(SparrowArgumentKeys.IS_CALLBACK, true);
                })
                .appendHandler(PlayerSelectorParserMessagingHandler.instance());
    }
}
