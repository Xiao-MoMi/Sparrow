package net.momirealms.sparrow.bukkit.user;

import net.momirealms.sparrow.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class BukkitOfflineUser implements User<OfflinePlayer> {

    protected final UUID uniqueId;

    public BukkitOfflineUser(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uniqueId);
    }

    @Override
    public boolean isOnline() {
        return getPlayer().isOnline();
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitOfflineUser that = (BukkitOfflineUser) o;
        return Objects.equals(uniqueId, that.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
}
