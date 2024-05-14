package net.momirealms.sparrow.common.feature.item;

import net.momirealms.sparrow.common.plugin.SparrowPlugin;

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
    public I getItem() {
        return factory.getItem(item);
    }

    @Override
    public I load() {
        return factory.load(item);
    }
}
