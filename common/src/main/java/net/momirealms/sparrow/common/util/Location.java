package net.momirealms.sparrow.common.util;

public class Location {

    private final double x;
    private final double y;
    private final double z;
    private final String world;

    public static Location of(double x, double y, double z, String world) {
        return new Location(x, y, z, world);
    }

    public Location(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }
}
