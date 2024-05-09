package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.common.gameplay.AdvancementType;
import net.momirealms.sparrow.heart.SparrowHeart;
import net.momirealms.sparrow.heart.argument.HandSlot;
import net.momirealms.sparrow.heart.argument.NamedTextColor;
import net.momirealms.sparrow.heart.feature.highlight.HighlightBlocks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class SparrowNMSProxy {

    private final SparrowHeart heart;

    public SparrowNMSProxy() {
        this.heart = SparrowHeart.getInstance();
    }

    public static SparrowNMSProxy getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final SparrowNMSProxy INSTANCE = new SparrowNMSProxy();
    }

    public void sendToast(@NotNull Player player, @NotNull ItemStack icon, @NotNull String jsonMessage, @NotNull AdvancementType advancementType) {
        requireNonNull(advancementType, "advancementType");
        requireNonNull(jsonMessage, "jsonMessage");
        requireNonNull(player, "player");
        heart.sendToast(player, icon, jsonMessage, advancementType.name());
    }

    public void sendTitle(@NotNull Player player, @Nullable String jsonTitle, @Nullable String jsonSubtitle, int fadeIn, int stay, int fadeOut) {
        requireNonNull(player, "player");
        heart.sendTitle(player, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
    }

    public void sendActionBar(@NotNull Player player, @NotNull String json) {
        requireNonNull(json, "json");
        requireNonNull(player, "player");
        heart.sendActionBar(player, json);
    }

    public void sendTotemAnimation(@NotNull Player player, @NotNull ItemStack totem) {
        requireNonNull(totem, "totem");
        requireNonNull(player, "player");
        if (totem.getType() != Material.TOTEM_OF_UNDYING) throw new IllegalArgumentException("ItemStack should be a totem");
        heart.sendTotemAnimation(player, totem);
    }

    public void sendCredits(@NotNull Player player) {
        requireNonNull(player, "player");
        heart.sendCredits(player);
    }

    public void sendDemo(@NotNull Player player) {
        requireNonNull(player, "player");
        heart.sendDemo(player);
    }

    public void openCustomInventory(@NotNull Player player, @NotNull Inventory inventory, @NotNull String titleTitle) {
        requireNonNull(player, "player");
        requireNonNull(inventory, "inventory");
        requireNonNull(titleTitle, "titleTitle");
        heart.openCustomInventory(player, inventory, titleTitle);
    }

    public void updateInventoryTitle(@NotNull Player player, @NotNull String titleTitle) {
        requireNonNull(player, "player");
        requireNonNull(titleTitle, "titleTitle");
        heart.updateInventoryTitle(player, titleTitle);
    }

    public void swingHand(@NotNull Player player, @NotNull HandSlot slot) {
        requireNonNull(player, "player");
        requireNonNull(slot, "slot");
        heart.swingHand(player, slot);
    }

    public EnchantmentOffer[] getEnchantmentOffers(Player player, ItemStack itemToEnchant, int shelves) {
        requireNonNull(player, "player");
        requireNonNull(itemToEnchant, "itemToEnchant");
        return heart.getEnchantmentOffers(player, itemToEnchant, shelves);
    }

    public HighlightBlocks highlightBlocks(Player player, NamedTextColor color, Location... locations) {
        return heart.highlightBlocks(player, color, locations);
    }
}
