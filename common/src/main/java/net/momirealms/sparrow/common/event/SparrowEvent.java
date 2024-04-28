package net.momirealms.sparrow.common.event;

import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import org.jetbrains.annotations.NotNull;

public interface SparrowEvent {

    /**
     * Get the plugin instance this event was dispatched from
     */
    @NotNull
    SparrowPlugin getSparrowPlugin();

    /**
     * Gets the type of the event.
     *
     * @return the type of the event
     */
    @NotNull
    Class<? extends SparrowEvent> getEventType();
}
