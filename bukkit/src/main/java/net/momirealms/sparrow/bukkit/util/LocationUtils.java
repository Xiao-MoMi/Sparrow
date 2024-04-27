package net.momirealms.sparrow.bukkit.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class LocationUtils {

    private LocationUtils() {}

    public static Location toBukkitLocation(net.momirealms.sparrow.common.util.Location location) {
        return new Location(
                Preconditions.checkNotNull(Bukkit.getWorld(location.getWorld()), "World " + location.getWorld() + " doesn't exist"),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }
}
