package net.momirealms.sparrow.bukkit.util;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.momirealms.sparrow.common.feature.skull.Skull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ItemStackUtils {
    private ItemStackUtils() {
    }

    /**
     * Generates a skull item with the specified skull.
     * <p>
     * <b>Note! This method will block thread and should be called asynchronously</b>.
     *
     * @param skull The skull to generate the item with.
     * @param amount The amount of items to generate.
     * @return The generated skull item.
     */
    @NotNull
    @Blocking
    @Contract(pure = true)
    public static ItemStack createSkullItem(@NotNull Skull skull, int amount) {
        return createSkullItemAsync(skull, amount).join();
    }

    /**
     * Generates a skull item with the specified skull asynchronously.
     * @param skull The skull to generate the item with.
     * @param amount The amount of items to generate.
     * @return The generated skull item.
     */
    @NotNull
    @Contract(pure = true)
    public static CompletableFuture<@NotNull ItemStack> createSkullItemAsync(@NotNull Skull skull, int amount) {
        return skull.getBase64().thenApply(base64 -> {
            final ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD, amount);
            changeSkullItem(skullItem, base64);
            return skullItem;
        });
    }

    private static void changeSkullItem(@NotNull ItemStack skullItem, @NotNull String base64) {
        NBT.modify(skullItem, nbt -> {
            final ReadWriteNBT skullOwnerCompound = nbt.getOrCreateCompound("SkullOwner");
            skullOwnerCompound.setUUID("Id", UUID.nameUUIDFromBytes(base64.getBytes()));

            skullOwnerCompound.getOrCreateCompound("Properties")
                    .getCompoundList("textures")
                    .addCompound()
                    .setString("Value", base64);
        });
    }
}
