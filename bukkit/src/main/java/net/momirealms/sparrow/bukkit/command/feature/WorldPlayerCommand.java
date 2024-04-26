package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.locale.Message;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.parser.WorldParser;

public class WorldPlayerCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "world_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("world", WorldParser.worldParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    World world = commandContext.get("world");
                    EntityUtils.changeWorld(commandContext.sender(), world);
                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                            .wrap(commandContext.sender())
                            .sendMessage(
                                    TranslationManager.render(
                                            Message.COMMANDS_PLAYER_WORLD_SUCCESS
                                                    .arguments(Component.text(world.getName()))
                                                    .build()
                                    ),
                                    true
                            );
                });
    }
}
