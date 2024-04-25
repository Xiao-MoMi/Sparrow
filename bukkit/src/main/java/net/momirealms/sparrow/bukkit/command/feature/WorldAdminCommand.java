package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.util.TypeUtils;

public class WorldAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "world_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("world", WorldParser.worldParser())
                .optional("entity", MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    World world = commandContext.get("world");
                    MultipleEntitySelector selector = commandContext.getOrDefault("entity", null);
                    if (selector == null) {
                        if (commandContext.sender() instanceof Player player) {
                            EntityUtils.changeWorld(player, world);
                        } else {
                            SparrowBukkitPlugin.getInstance().getBootstrap().getPluginLogger().severe(
                                    String.format("%s is not allowed to execute that command. Must be of type %s", commandContext.sender().getClass().getSimpleName(), TypeUtils.simpleName(Player.class))
                            );
                        }
                    } else {
                        for (Entity entity : selector.values()) {
                            EntityUtils.changeWorld(entity, world);
                        }
                    }
                });
    }
}
