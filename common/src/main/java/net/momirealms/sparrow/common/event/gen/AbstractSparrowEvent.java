package net.momirealms.sparrow.common.event.gen;

import net.momirealms.sparrow.common.event.SparrowEvent;
import net.momirealms.sparrow.common.plugin.plugin.SparrowPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;

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

    public MethodHandles.Lookup mhl() {
        throw new UnsupportedOperationException();
    }
}
