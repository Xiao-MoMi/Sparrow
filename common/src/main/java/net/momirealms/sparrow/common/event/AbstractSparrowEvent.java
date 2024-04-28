package net.momirealms.sparrow.common.event;

import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSparrowEvent implements SparrowEvent {

    private final SparrowPlugin plugin;

    protected AbstractSparrowEvent(SparrowPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public SparrowPlugin getSparrowPlugin() {
        return plugin;
    }
}
