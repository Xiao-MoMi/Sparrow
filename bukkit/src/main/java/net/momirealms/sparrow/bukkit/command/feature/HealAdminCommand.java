package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.util.EntityUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;

public class HealAdminCommand extends AbstractCommand {
    @Override
    public String getFeatureID() {
        return "heal_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .handler(commandContext -> {
                    Selector<Player> selector = commandContext.get("player");
                    for (Player player : selector.values()) {
                        EntityUtil.heal(player);
                    }
                });
    }
}
