package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.WorldParser;

import java.util.List;

public class WorldPlayerCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "world_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("world", WorldParser.worldParser())
                .handler(commandContext -> {
                    World world = commandContext.get("world");
                    EntityUtils.changeWorld(commandContext.sender(), world);
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_WORLD_SUCCESS);
                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text(world.getName())));
                });
    }
}
