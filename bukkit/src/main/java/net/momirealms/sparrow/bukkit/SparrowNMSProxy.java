package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.common.gameplay.AdvancementType;
import net.momirealms.sparrow.heart.SparrowVessel;
import net.momirealms.sparrow.heart.heart.SparrowHeart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SparrowNMSProxy {

    private static SparrowHeart getNMSInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final SparrowHeart INSTANCE = new SparrowVessel().getHeart();
    }

    public void sendToast(Player player, ItemStack icon,  String jsonMessage, AdvancementType advancementType) {
        getNMSInstance().sendToast(player, icon, jsonMessage, advancementType.name());
    }

    public void sendTitle(Player player, String jsonTitle, String jsonSubtitle, int fadeIn, int stay, int fadeOut) {
        getNMSInstance().sendTitle(player, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
    }

    public void sendActionBar(Player player, String json) {
        getNMSInstance().sendActionBar(player, json);
    }
}
