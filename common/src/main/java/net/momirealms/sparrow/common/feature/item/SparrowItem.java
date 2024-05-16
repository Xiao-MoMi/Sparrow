package net.momirealms.sparrow.common.feature.item;

import net.momirealms.sparrow.common.feature.skull.SkullData;

import java.util.List;
import java.util.Optional;

public interface SparrowItem<I> {

    SparrowItem<I> customModelData(Integer data);

    Optional<Integer> customModelData();

    SparrowItem<I> displayName(String displayName);

    Optional<String> displayName();

    SparrowItem<I> lore(List<String> lore);

    Optional<List<String>> lore();

    SparrowItem<I> skull(SkullData data);

    I getItem();

    I load();

    I loadCopy();
}
