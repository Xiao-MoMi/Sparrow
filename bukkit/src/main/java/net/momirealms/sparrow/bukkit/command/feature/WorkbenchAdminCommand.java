package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.parser.PlayerParser;

public class WorkbenchAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "workbench_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(Command.Builder<CommandSender> builder) {
        return builder
                .required("player", PlayerParser.playerParser())
                .handler(commandContext -> {
                    Player player = commandContext.get("player");
                    player.openWorkbench(player.getLocation(), true);
                });
    }
}
