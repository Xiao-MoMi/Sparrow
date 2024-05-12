package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.command.MessagingCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.util.CommandUtils;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;

public class AnvilAdminCommand extends MessagingCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "anvil_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .flag(SparrowFlagKeys.TITLE_FLAG)
                .flag(SparrowFlagKeys.LEGACY_COLOR_FLAG)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    var players = selector.values();
                    boolean legacy = commandContext.flags().hasFlag(SparrowFlagKeys.LEGACY_COLOR_FLAG);
                    boolean customTitle = commandContext.flags().hasFlag(SparrowFlagKeys.TITLE_FLAG);
                    for (Player player : players) {
                        player.openAnvil(null, true);
                        if (customTitle) {
                            String containerTitle = commandContext.flags().getValue(SparrowFlagKeys.TITLE_FLAG).get();
                            String json = AdventureHelper.componentToJson(AdventureHelper.getMiniMessage().deserialize(
                                    legacy ? AdventureHelper.legacyToMiniMessage(containerTitle) : containerTitle
                            ));
                            SparrowNMSProxy.getInstance().updateInventoryTitle(
                                    player, json
                            );
                        }
                    }
                    CommandUtils.storeEntitySelectorMessage(commandContext, selector,
                            Pair.of(MessageConstants.COMMANDS_ADMIN_ANVIL_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_ANVIL_SUCCESS_MULTIPLE)
                    );
                });
    }
}
