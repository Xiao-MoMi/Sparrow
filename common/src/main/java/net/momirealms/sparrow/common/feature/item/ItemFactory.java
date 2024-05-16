package net.momirealms.sparrow.common.feature.item;

import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;

import java.util.List;
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

    protected abstract void update(R item);

    protected abstract I load(R item);

    protected abstract I getItem(R item);

    protected abstract I loadCopy(R item);

    protected abstract void customModelData(R rtag, Integer data);

    protected abstract Optional<Integer> customModelData(R rtag);

    protected abstract void displayName(R rtag, String json);

    protected abstract Optional<String> displayName(R rtag);

    protected abstract void skull(R rtag, SkullData skullData);

    protected abstract Optional<List<String>> lore(R item);

    protected abstract void lore(R item, List<String> lore);

    protected abstract boolean unbreakable(R item);

    protected abstract void unbreakable(R item, boolean unbreakable);

    protected abstract Optional<Boolean> glint(R item);

    protected abstract void glint(R item, Boolean glint);
}
