package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.StringParser;

public class BroadcastAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "broadcast_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("message", StringParser.greedyFlagYieldingStringParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .flag(manager.flagBuilder("legacy-color").withAliases("l"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    String message = commandContext.get("message");
                    boolean legacy = commandContext.flags().hasFlag("legacy-color");
                    for (Player player : selector.values()) {
                        SparrowBukkitPlugin.getInstance().getSenderFactory().wrap(player).sendMessage(AdventureHelper.getMiniMessage().deserialize(
                                legacy ? AdventureHelper.legacyToMiniMessage(message) : message
                        ));
                    }
                    if (!commandContext.flags().hasFlag("silent")) {

                    }
                });
    }
}
