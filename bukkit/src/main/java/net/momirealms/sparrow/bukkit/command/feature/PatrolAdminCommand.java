package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.user.BukkitPatrolManager;
import net.momirealms.sparrow.common.feature.patrol.PatrolManager;
import net.momirealms.sparrow.common.feature.patrol.Patrolable;
import net.momirealms.sparrow.bukkit.user.BukkitUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class PatrolAdminCommand extends AbstractCommand {
    @Override
    public String getFeatureID() {
        return "patrol_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .optional("players", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .handler(commandContext -> {
                    Optional<Selector<Player>> selector = commandContext.optional("players");
                    Collection<Player> players = selector.map(Selector::values).orElseGet(() -> {
                        if (commandContext.sender() instanceof Player) {
                            return Set.of((Player) commandContext.sender());
                        }
                        return Set.of();
                    });
                    if (players.isEmpty()) {
                        commandContext.sender().sendMessage(Component.translatable("commands.patrol.failed.no_players_found"));
                        return;
                    }

                    PatrolManager patrolManager = BukkitPatrolManager.getInstance();
                    for (Player player : players) {
                        @NotNull Patrolable target = patrolManager.getNextPatrolable(patrolable -> patrolable.getUniqueId().equals(player.getUniqueId()));

                        if (target instanceof BukkitUser targetUser) {
                            @Nullable Player targetPlayer = targetUser.getPlayer();
                            if (targetPlayer == null) {
                                commandContext.sender().sendMessage(Component.translatable("commands.patrol.failed.no_patrol_users"));
                                continue;
                            }
                            player.teleport(targetPlayer);
                        }
                    }
                });
    }
}
