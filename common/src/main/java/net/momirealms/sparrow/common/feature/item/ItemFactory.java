package net.momirealms.sparrow.common.feature.item;

import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;

import java.util.Objects;
import java.util.Optional;

public abstract class ItemFactory<P extends SparrowPlugin, R, I> {

    protected final P plugin;

    protected ItemFactory(P plugin) {
        this.plugin = plugin;
    }

    public SparrowItem<I> wrap(R item) {
        Objects.requireNonNull(item, "item");
        return new AbstractItem<>(this.plugin, this, item);
    }

    protected abstract void customModelData(R rtag, Integer data);

    protected abstract Optional<Integer> customModelData(R rtag);

    protected abstract void displayName(R rtag, String json);

    protected abstract Optional<String> displayName(R rtag);

    protected abstract void skull(R rtag, SkullData skullData);

    protected abstract void update(R item);

    protected abstract I load(R item);

    protected abstract I getItem(R item);
}
