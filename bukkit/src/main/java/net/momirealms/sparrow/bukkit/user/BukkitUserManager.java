package net.momirealms.sparrow.bukkit.user;

import net.momirealms.sparrow.common.user.UserManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitUserManager implements UserManager<BukkitOnlineUser> {

    private final Map<UUID, BukkitOnlineUser> users = new HashMap<>();

    private BukkitUserManager() {}

    private static class SingletonHolder {
        private static final BukkitUserManager INSTANCE = new BukkitUserManager();
    }

    public static BukkitUserManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public @NotNull BukkitOnlineUser getUser(UUID uniqueId) {
        return users.computeIfAbsent(uniqueId, BukkitOnlineUser::new);
    }
}
