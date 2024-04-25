package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.BooleanParser;

public class FlyAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "fly_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("fly", BooleanParser.booleanParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    boolean fly = commandContext.get("fly");
                    for (Player player : selector.values()) {
                        player.setAllowFlight(fly);
                        player.setFlying(fly);
                    }
                });
    }
}
