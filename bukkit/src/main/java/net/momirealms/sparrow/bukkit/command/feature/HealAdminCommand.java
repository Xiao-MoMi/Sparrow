package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;

public class HealAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "heal_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("entity", MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    Selector<Entity> selector = commandContext.get("entity");
                    for (Entity entity : selector.values()) {
                        EntityUtils.heal(entity);
                    }
                });
    }
}
