package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.heart.SparrowVessel;
import net.momirealms.sparrow.heart.heart.SparrowHeart;

public class SparrowCoreNMSBridge {

    private final SparrowHeart heart;

    public SparrowCoreNMSBridge() {
        this.heart = new SparrowVessel().getHeart();
    }

    public SparrowHeart getHeart() {
        return heart;
    }
}
