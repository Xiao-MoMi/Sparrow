package net.momirealms.sparrow.bukkit.util;

import org.bukkit.Location;

public final class LocationUtils {

    private LocationUtils() {}

    public static double euclideanDistance(Location location1, Location location2) {
        double sum = 0;
        sum += Math.pow(location1.getX() - location2.getX(), 2);
        sum += Math.pow(location1.getY() - location2.getY(), 2);
        sum += Math.pow(location1.getZ() - location2.getZ(), 2);
        return Math.sqrt(sum);
    }

    public static double manhattanDistance(Location location1, Location location2) {
        double sum = 0;
        sum += Math.abs(location1.getX() - location2.getX());
        sum += Math.abs(location1.getY() - location2.getY());
        sum += Math.abs(location1.getZ() - location2.getZ());
        return sum;
    }

    public static Location toBlockCenter(Location location) {
        Location centerLoc = location.clone();
        centerLoc.setX(location.getBlockX() + 0.5);
        centerLoc.setY(location.getBlockY() + 0.5);
        centerLoc.setZ(location.getBlockZ() + 0.5);
        return centerLoc;
    }

    public static Location toBlockLocation(Location location) {
        Location blockLoc = location.clone();
        blockLoc.setX(location.getBlockX());
        blockLoc.setY(location.getBlockY());
        blockLoc.setZ(location.getBlockZ());
        return blockLoc;
    }
}
