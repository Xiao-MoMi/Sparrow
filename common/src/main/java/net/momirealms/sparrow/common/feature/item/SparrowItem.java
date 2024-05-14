package net.momirealms.sparrow.common.feature.item;

import java.util.Optional;

public interface SparrowItem<I> {

    SparrowItem<I> customModelData(Integer data);

    Optional<Integer> customModelData();

    I getItem();

    I load();
}
