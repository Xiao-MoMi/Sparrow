package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
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
import org.incendo.cloud.parser.standard.IntegerParser;

public class EnchantmentTableAdminCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "enchantmenttable_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .flag(manager.flagBuilder("shelves").withComponent(IntegerParser.integerParser(0)))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .flag(SparrowFlagKeys.TITLE_FLAG)
                .flag(SparrowFlagKeys.LEGACY_COLOR_FLAG)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    boolean legacy = commandContext.flags().hasFlag(SparrowFlagKeys.LEGACY_COLOR_FLAG);
                    boolean customTitle = commandContext.flags().hasFlag(SparrowFlagKeys.TITLE_FLAG);
                    boolean customShelves = commandContext.flags().hasFlag("shelves");
                    var players = selector.values();
                    for (Player player : players) {
                        SparrowBukkitPlugin.getInstance().getEnchantManager().openEnchantmentTable(player, customShelves ? (int) commandContext.flags().getValue("shelves").get() : 0);
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
                            Pair.of(MessageConstants.COMMANDS_ADMIN_ENCHANTMENT_TABLE_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_ENCHANTMENT_TABLE_SUCCESS_MULTIPLE)
                    );
                });
    }
}
