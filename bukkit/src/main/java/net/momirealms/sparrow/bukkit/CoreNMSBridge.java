package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.heart.SparrowVessel;
import net.momirealms.sparrow.heart.heart.SparrowHeart;

public class CoreNMSBridge {

    private final SparrowHeart heart;

    public CoreNMSBridge() {
        this.heart = new SparrowVessel().getHeart();
    }

    public SparrowHeart getHeart() {
        return heart;
    }
}
