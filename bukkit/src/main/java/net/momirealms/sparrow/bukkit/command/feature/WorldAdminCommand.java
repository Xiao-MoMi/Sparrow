package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.util.CommandUtils;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;

import java.util.List;

public class WorldAdminCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "world_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.ENTITY_SELECTOR, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .required("world", WorldParser.worldParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    World world = commandContext.get("world");
                    MultipleEntitySelector selector = commandContext.get(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
                    var entities = selector.values();
                    for (Entity entity : entities) {
                        EntityUtils.changeWorld(entity, world);
                    }
                    CommandUtils.storeSelectorMessage(commandContext, selector,
                            Pair.of(MessageConstants.COMMANDS_ADMIN_WORLD_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_WORLD_SUCCESS_MULTIPLE),
                            Pair.of(
                                    () -> List.of(Component.text(entities.iterator().next().getName()), Component.text(world.getName())),
                                    () -> List.of(Component.text(entities.size()), Component.text(world.getName()))
                            )
                    );
                });
    }
}
