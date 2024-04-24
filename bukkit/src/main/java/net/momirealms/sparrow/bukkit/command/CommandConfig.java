package net.momirealms.sparrow.bukkit.command;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;

import java.util.ArrayList;
import java.util.List;

public class CommandConfig {

    private final boolean enable;
    private final List<String> usages;
    private final String permission;

    public CommandConfig(boolean enable, List<String> usages, String permission) {
        this.enable = enable;
        this.usages = usages;
        this.permission = permission;
    }

    public boolean isEnable() {
        return enable;
    }

    public List<String> getUsages() {
        return usages;
    }

    public String getPermission() {
        return permission;
    }

    public List<Command.@NonNull Builder<CommandSender>> builders(BukkitCommandManager<CommandSender> manager) {
        if (!isEnable() || getUsages().size() == 0)
            return List.of();
        ArrayList<Command.@NonNull Builder<CommandSender>> builders = new ArrayList<>();
        for (String usage : getUsages()) {
            if (usage.startsWith("/")) {
                String command = usage.substring(1);
                String[] split = command.split(" ");
                if (split.length > 2) {
                    throw new IllegalArgumentException("Invalid usage: " + usage);
                }
                for (String sub : split) {
                    if (sub.isEmpty() || sub.isBlank()) {
                        throw new IllegalArgumentException("Invalid usage: " + usage);
                    }
                }
                var builder = manager.commandBuilder(split[0]);
                if (split.length == 2) {
                    builder = builder.literal(split[1]);
                }
                if (getPermission() != null) {
                    builder = builder.permission(getPermission());
                }
                builders.add(builder);
            }
        }
        return builders;
    }
}
