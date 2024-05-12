package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class ReloadAdminCommand extends BukkitCommandFeature<CommandSender> {

    public ReloadAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "reload_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    long time1 = System.currentTimeMillis();
                    SparrowBukkitPlugin.getInstance().reload();
                    String timeTook = String.valueOf(System.currentTimeMillis() - time1);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_RELOAD_SUCCESS, Component.text(timeTook));
                });
    }
}
