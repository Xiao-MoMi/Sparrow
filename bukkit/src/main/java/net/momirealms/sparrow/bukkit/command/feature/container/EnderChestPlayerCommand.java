package net.momirealms.sparrow.bukkit.command.feature.container;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class EnderChestPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public EnderChestPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "enderchest_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    SparrowNMSProxy.getInstance().openCustomInventory(player, player.getEnderChest(), AdventureHelper.componentToJson(Component.translatable("container.enderchest")));
                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ENDER_CHEST_SUCCESS);
                });
    }
}
