package net.momirealms.sparrow.bukkit.command.feature.container;

import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.util.PlaceholderUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
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

public class AnvilAdminCommand extends BukkitCommandFeature<CommandSender> {

    public AnvilAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "anvil_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, false)
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .flag(SparrowFlagKeys.TITLE_FLAG)
                .flag(SparrowFlagKeys.LEGACY_COLOR_FLAG)
                .flag(SparrowFlagKeys.PARSE_FLAG)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    var players = selector.values();
                    boolean legacy = commandContext.flags().hasFlag(SparrowFlagKeys.LEGACY_COLOR_FLAG);
                    boolean customTitle = commandContext.flags().hasFlag(SparrowFlagKeys.TITLE_FLAG);
                    boolean parse = commandContext.flags().hasFlag(SparrowFlagKeys.PARSE_FLAG);
                    for (Player player : players) {
                        player.openAnvil(null, true);
                        if (customTitle) {
                            String containerTitle = commandContext.flags().getValue(SparrowFlagKeys.TITLE_FLAG).get();
                            if (parse) {
                                containerTitle = PlaceholderUtils.parse(player, containerTitle);
                            }
                            String json = AdventureHelper.componentToJson(AdventureHelper.getMiniMessage().deserialize(
                                    legacy ? AdventureHelper.legacyToMiniMessage(containerTitle) : containerTitle
                            ));
                            SparrowNMSProxy.getInstance().updateInventoryTitle(
                                    player, json
                            );
                        }
                    }
                    var pair = resolveSelector(selector, MessageConstants.COMMANDS_ADMIN_ANVIL_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_ANVIL_SUCCESS_MULTIPLE);
                    handleFeedback(commandContext, pair.left(), pair.right());
                });
    }
}
