package net.momirealms.sparrow.bukkit.event;

import net.momirealms.sparrow.common.event.AbstractCancellable;
import net.momirealms.sparrow.common.event.SparrowEvent;
import net.momirealms.sparrow.common.event.type.CommandFeedbackEvent;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import org.jetbrains.annotations.NotNull;

public class BukkitCommandFeedbackEvent extends AbstractCancellable implements CommandFeedbackEvent {

    private final String key;

    public BukkitCommandFeedbackEvent(SparrowPlugin plugin, String key) {
        super(plugin);
        this.key = key;
    }

    @NotNull
    @Override
    public Class<? extends SparrowEvent> getEventType() {
        return CommandFeedbackEvent.class;
    }

    @Override
    public String key() {
        return this.key;
    }
}
