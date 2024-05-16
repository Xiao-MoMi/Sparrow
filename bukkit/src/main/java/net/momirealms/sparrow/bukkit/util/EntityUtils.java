package net.momirealms.sparrow.bukkit.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class EntityUtils {

    private EntityUtils() {}

    /**
     * Set rotation to look at other entity.
     * If the entity is living entity, look at their eyes.
     * If the entity is not living entity, look at origin.
     * @param self the entity to set rotation
     * @param target the target entity
     */
    public static void look(@NotNull Entity self, @NotNull Entity target) {
        if (self == target) return;

        Location targetLocation = (target instanceof LivingEntity) ?
                ((LivingEntity) target).getEyeLocation() :
                target.getLocation();
        orientTowards(self, targetLocation);
    }

    /**
     * Set rotation to look at certain location
     * @param self the entity to set rotation
     * @param location the target location
     */
    public static void look(@NotNull Entity self, @NotNull Location location) {
        orientTowards(self, location);
    }

    /**
     * Makes the player look in one of the four directions.
     * @param self the player entity
     * @param face the direction to look at (north, east, south, or west)
     */
    public static void look(@NotNull Entity self, @NotNull String face) {
        float yaw;
        switch (face) {
            case "north" -> yaw = -180;
            case "east" -> yaw = -90;
            case "south" -> yaw = 0;
            case "west" -> yaw = 90;
            default -> {
                return;
            }
        }
        self.setRotation(yaw, self.getPitch());
    }

    private static void orientTowards(@NotNull Entity self, @NotNull Location targetLocation) {
        Location selfLocation = self.getLocation();
        double eyeHeight = self instanceof LivingEntity ? ((LivingEntity) self).getEyeHeight() : 0;
        double deltaX = targetLocation.getX() - selfLocation.getX();
        double deltaY = targetLocation.getY() - (selfLocation.getY() + eyeHeight);
        double deltaZ = targetLocation.getZ() - selfLocation.getZ();

        double yaw = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;
        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double pitch = -Math.toDegrees(Math.atan(deltaY / distanceXZ));

        self.setRotation((float) yaw, (float) pitch);
    }

    /**
     * Heals the entity to full health.
     * If the entity is dead, they will be resurrected.
     * If the entity is a player, their food level and saturation will also be set to full.
     *
     * @param entity the entity to heal
     */
    public static void heal(@NotNull Entity entity) {
        requireNonNull(entity, "entity");
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            if (livingEntity instanceof Player player) {
                player.setFoodLevel(20);
                player.setSaturation(10f);
            }
        }
    }

    public static void toTopBlockPosition(Entity entity) {
        var location = entity.getLocation();
        var block = location.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ());
        var newLocation = location.clone();
        newLocation.setY(block.isPassable() ? block.getY() : block.getY() + 1);
        entity.teleport(newLocation);
    }
    
    /**
     * Changes the world of the entity. The location of the entity will be adjusted to the new world.
     *
     * @param entity the entity to change the world of.
     * @param to the world to change to.
     */
    public static void changeWorld(@NotNull Entity entity, @NotNull World to) {
        requireNonNull(entity, "entity");
        requireNonNull(to, "to");
        var fromEnv = entity.getWorld().getEnvironment();
        var toEnv = to.getEnvironment();
        var location = entity.getLocation();
        var x = location.getX();
        var y = location.getY();
        var z = location.getZ();
        if (fromEnv != World.Environment.NETHER && toEnv == World.Environment.NETHER) {
            x /= 8;
            z /= 8;
        } else if (fromEnv == World.Environment.NETHER && toEnv != World.Environment.NETHER) {
            x *= 8;
            z *= 8;
        }
        int height = (int) Math.ceil(entity.getHeight());
        Location toLocation = new Location(to, x, y, z, location.getYaw(), location.getPitch());
        int space = 0;
        while (true) {
            Block block = toLocation.getBlock();
            if (block.isPassable() && !block.isLiquid()) {
                if (space + 1 >= height) {
                    break;
                } else {
                    space++;
                    toLocation.add(0,1,0);
                }
            } else {
                space = 0;
                toLocation.add(0,1,0);
            }
        }
        entity.teleport(toLocation.subtract(0,height - 1,0));
    }
}
