package net.momirealms.sparrow.bukkit.feature.user;

import net.momirealms.sparrow.common.feature.user.UserManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitUserManager implements UserManager<BukkitUser> {
    private static BukkitUserManager instance;
    private final Map<UUID, BukkitUser> users = new HashMap<>();

    private BukkitUserManager() {}

    public static BukkitUserManager getInstance() {
        if (instance == null) {
            instance = new BukkitUserManager();
        }
        return instance;
    }

    public @NotNull BukkitUser getUser(UUID uniqueId) {
        return users.computeIfAbsent(uniqueId, BukkitUser::new);
    }
}
