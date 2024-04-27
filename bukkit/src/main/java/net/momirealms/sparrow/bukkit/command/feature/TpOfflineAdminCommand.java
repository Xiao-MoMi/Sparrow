package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
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

public class TpOfflineAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "tpoffline_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("entity", MultipleEntitySelectorParser.multipleEntitySelectorParser(false))
                .required("player", StringParser.stringParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    boolean silent = commandContext.flags().hasFlag("silent");
                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(commandContext.get("player"));
                    if (player == null || player.getLocation() == null) {
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_TP_OFFLINE_FAILED_NEVER_PLAYED
                                                            .arguments(Component.text((String) commandContext.get("player")))
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                        return;
                    }

                    MultipleEntitySelector selector = commandContext.get("entity");
                    var entities = selector.values();

                    for (Entity entity : entities) {
                        entity.teleport(player.getLocation());
                    }

                    if (entities.size() == 1) {
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_TP_OFFLINE_SUCCESS_SINGLE
                                                            .arguments(Component.text(entities.iterator().next().getName()), Component.text(Optional.ofNullable(player.getName()).orElse(String.valueOf(player.getUniqueId()))))
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                    } else {
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_TP_OFFLINE_SUCCESS_MULTIPLE
                                                            .arguments(Component.text(entities.size()), Component.text(Optional.ofNullable(player.getName()).orElse(String.valueOf(player.getUniqueId()))))
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                    }
                });
    }
}
