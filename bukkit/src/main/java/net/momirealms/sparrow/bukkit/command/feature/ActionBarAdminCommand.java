package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
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
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    String actionBarContent = commandContext.get("actionbar");
                    for (Player player : selector.values()) {
                        String json = GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(actionBarContent));
                        SparrowBukkitPlugin.getInstance().getCoreNMSBridge().getHeart().sendActionBar(
                                player, json
                        );
                    }
                });
    }
}
