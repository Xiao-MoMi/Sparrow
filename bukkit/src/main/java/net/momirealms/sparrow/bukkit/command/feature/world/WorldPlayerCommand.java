package net.momirealms.sparrow.bukkit.command.feature.world;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.WorldParser;

import java.util.List;

public class WorldPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public WorldPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "world_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .optional("world", WorldParser.worldParser())
                .handler(commandContext -> {
                    World world = commandContext.getOrSupplyDefault("world", () -> {
                        World currentWorld = commandContext.sender().getWorld();
                        List<World> worlds = Bukkit.getWorlds();
                        for (int i = 0; i < worlds.size(); i++) {
                            if (currentWorld == worlds.get(i)) {
                                if (i + 1 < worlds.size()) {
                                    return worlds.get(i + 1);
                                } else {
                                    return worlds.get(0);
                                }
                            }
                        }
                        throw new RuntimeException("No world found");
                    });
                    EntityUtils.changeWorld(commandContext.sender(), world);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_WORLD_SUCCESS, Component.text(world.getName()));
                });
    }
}
