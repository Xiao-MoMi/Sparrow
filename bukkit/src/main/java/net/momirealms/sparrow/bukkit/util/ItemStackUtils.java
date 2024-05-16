package net.momirealms.sparrow.bukkit.util;

import com.saicone.rtag.RtagMirror;
import com.saicone.rtag.item.ItemObject;
import com.saicone.rtag.tag.TagCompound;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.skull.SkullData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ItemStackUtils {
    private ItemStackUtils() {
    }

    public static void applySkull(@NotNull ItemStack skull, @Nullable SkullData skullData) {
        try {
            SparrowBukkitPlugin.getInstance().getItemFactory().wrap(skull)
                    .skull(skullData)
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Object> toReadableMap(ItemStack item) {
        return toMap(item);
    }

    private static Map<String, Object> toMap(ItemStack object) {
        return TagCompound.getValue(RtagMirror.INSTANCE, toCompound(object));
    }

    private static Object toCompound(ItemStack object) {
        if (object == null) {
            return null;
        } else {
            Object compound = extract(object);
            return TagCompound.isTagCompound(compound) ? compound : null;
        }
    }

    private static Object extract(ItemStack object) {
        return ItemObject.save(ItemObject.asNMSCopy(object));
    }
}
