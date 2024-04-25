package net.momirealms.sparrow.bukkit.user;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// TODO: This is a placeholder class for the UserManager. This class will be used to manage all the users on the server.
public class BukkitUserManager {
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
