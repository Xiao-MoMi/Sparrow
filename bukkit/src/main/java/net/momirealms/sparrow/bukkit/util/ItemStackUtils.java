package net.momirealms.sparrow.bukkit.util;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.momirealms.sparrow.common.feature.skull.SkullData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemStackUtils {
    private ItemStackUtils() {
    }

    public static void applySkull(@NotNull ItemStack skull, @Nullable SkullData skullData) {
        if (skull.getType() != Material.PLAYER_HEAD) throw new RuntimeException("Not a skull");
        if (skullData == null) return;
        NBT.modify(skull, nbt -> {
            final ReadWriteNBT skullOwnerCompound = nbt.getOrCreateCompound("SkullOwner");
            skullOwnerCompound.setUUID("Id", skullData.getUUID());
            skullOwnerCompound.setString("Name", skullData.getOwner());
            skullOwnerCompound.getOrCreateCompound("Properties")
                    .getCompoundList("textures")
                    .addCompound()
                    .setString("Value", skullData.getTextureBase64());
        });
    }
}
