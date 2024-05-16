package net.momirealms.sparrow.common.feature.item;

import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;

import java.util.List;
import java.util.Optional;

public class AbstractItem<R, I> implements SparrowItem<I> {

    private final SparrowPlugin plugin;
    private final ItemFactory<?, R, I> factory;
    private final R item;

    AbstractItem(SparrowPlugin plugin, ItemFactory<?, R, I> factory, R item) {
        this.plugin = plugin;
        this.factory = factory;
        this.item = item;
    }

    @Override
    public SparrowItem<I> customModelData(Integer data) {
        factory.customModelData(item, data);
        return this;
    }

    @Override
    public Optional<Integer> customModelData() {
        return factory.customModelData(item);
    }

    @Override
    public Optional<String> displayName() {
        return factory.displayName(item);
    }

    @Override
    public SparrowItem<I> lore(List<String> lore) {
        factory.lore(item, lore);
        return this;
    }

    @Override
    public Optional<List<String>> lore() {
        return factory.lore(item);
    }

    @Override
    public SparrowItem<I> displayName(String displayName) {
        factory.displayName(item, displayName);
        return this;
    }

    @Override
    public SparrowItem<I> skull(SkullData data) {
        factory.skull(item, data);
        return this;
    }

    @Override
    public I getItem() {
        return factory.getItem(item);
    }

    @Override
    public I load() {
        return factory.load(item);
    }

    @Override
    public I loadCopy() {
        return factory.loadCopy(item);
    }

    @Override
    public void update() {
        factory.update(item);
    }
}
