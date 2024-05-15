package net.momirealms.sparrow.common.feature.item;

import net.momirealms.sparrow.common.feature.skull.SkullData;

import java.util.Optional;

public interface SparrowItem<I> {

    SparrowItem<I> customModelData(Integer data);

    Optional<Integer> customModelData();

    Optional<String> displayName();

    SparrowItem<I> displayName(String displayName);

    SparrowItem<I> skull(SkullData data);

    I getItem();

    I load();
}
