package net.momirealms.sparrow.bukkit.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class EntityUtils {
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
                player.setSaturation(20);
            }
        }
    }
}
