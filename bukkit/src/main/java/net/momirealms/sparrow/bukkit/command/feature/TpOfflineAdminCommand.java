package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Optional;

public class TpOfflineAdminCommand extends BukkitCommandFeature<CommandSender> {

    public TpOfflineAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "tpoffline_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.ENTITY_SELECTOR, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_ENTITY_SELECTOR, false)
                .required("player", StringParser.stringParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(commandContext.get("player"));
                    if (player == null || player.getLocation() == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_TP_OFFLINE_FAILED_NEVER_PLAYED, Component.text((String) commandContext.get("player")));
                        return;
                    }
                    MultipleEntitySelector selector = commandContext.get(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
                    var entities = selector.values();
                    for (Entity entity : entities) {
                        entity.teleport(player.getLocation());
                    }
                    var pair = resolveSelector(selector, MessageConstants.COMMANDS_ADMIN_TP_OFFLINE_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_TP_OFFLINE_SUCCESS_MULTIPLE);
                    handleFeedback(commandContext, pair.left(), pair.right(), Component.text(Optional.ofNullable(player.getName()).orElse(String.valueOf(player.getUniqueId()))));
                });
    }
}
