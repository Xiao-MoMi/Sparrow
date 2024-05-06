package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.common.gameplay.AdvancementType;
import net.momirealms.sparrow.heart.SparrowVessel;
import net.momirealms.sparrow.heart.heart.SparrowHeart;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class SparrowNMSProxy {

    private static SparrowHeart getNMSInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final SparrowHeart INSTANCE = new SparrowVessel().getHeart();
    }

    public void sendToast(@NotNull Player player, @NotNull ItemStack icon, @NotNull String jsonMessage, @NotNull AdvancementType advancementType) {
        requireNonNull(advancementType, "advancementType");
        requireNonNull(jsonMessage, "jsonMessage");
        requireNonNull(player, "player");
        getNMSInstance().sendToast(player, icon, jsonMessage, advancementType.name());
    }

    public void sendTitle(@NotNull Player player, @NotNull String jsonTitle, @NotNull String jsonSubtitle, int fadeIn, int stay, int fadeOut) {
        requireNonNull(jsonTitle, "jsonTitle");
        requireNonNull(jsonSubtitle, "jsonSubtitle");
        requireNonNull(player, "player");
        getNMSInstance().sendTitle(player, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
    }

    public void sendActionBar(@NotNull Player player, @NotNull String json) {
        requireNonNull(json, "json");
        requireNonNull(player, "player");
        getNMSInstance().sendActionBar(player, json);
    }

    public void sendTotemAnimation(@NotNull Player player, @NotNull ItemStack totem) {
        requireNonNull(totem, "totem");
        requireNonNull(player, "player");
        if (totem.getType() != Material.TOTEM_OF_UNDYING) throw new IllegalArgumentException("ItemStack should be a totem");
        getNMSInstance().sendTotemAnimation(player, totem);
    }

    public void sendCredits(Player player) {
        requireNonNull(player, "player");
        getNMSInstance().sendCredits(player);
    }

    public void sendDemo(Player player) {
        requireNonNull(player, "player");
        getNMSInstance().sendDemo(player);
    }
}
