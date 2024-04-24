package net.momirealms.sparrow.bukkit.command.feature;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.sparrow.bukkit.BukkitPacketManager;
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
                    WrapperPlayServerActionBar actionBar = new WrapperPlayServerActionBar(MiniMessage.miniMessage().deserialize(actionBarContent));
                    BukkitPacketManager packetManager = SparrowBukkitPlugin.getInstance().getBukkitPacketManager();
                    for (Player player : selector.values()) {
                        packetManager.sendPacket(player, actionBar);
                    }
                });
    }
}
