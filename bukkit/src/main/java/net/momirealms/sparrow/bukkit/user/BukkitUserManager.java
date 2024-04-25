package net.momirealms.sparrow.bukkit.user;

import net.momirealms.sparrow.common.user.UserManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitUserManager implements UserManager<BukkitOnlineUser> {

    private static BukkitUserManager instance;
    private final Map<UUID, BukkitOnlineUser> users = new HashMap<>();

    private BukkitUserManager() {}

    public static BukkitUserManager getInstance() {
        if (instance == null) {
            instance = new BukkitUserManager();
        }
        return instance;
    }

    public @NotNull BukkitOnlineUser getUser(UUID uniqueId) {
        return users.computeIfAbsent(uniqueId, BukkitOnlineUser::new);
    }
}
