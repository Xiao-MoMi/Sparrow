package net.momirealms.sparrow.common.event;

import net.momirealms.sparrow.common.event.bus.EventBus;
import net.momirealms.sparrow.common.event.bus.SimpleEventBus;
import net.momirealms.sparrow.common.event.registry.EventRegistry;
import net.momirealms.sparrow.common.event.registry.SimpleEventRegistry;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;

import java.util.OptionalInt;

public class EventDispatcher {

    private final EventBus<SparrowEvent> eventBus;
    private final EventRegistry<SparrowEvent> registry;

    public EventDispatcher(SparrowPlugin plugin) {
        this.registry = new SimpleEventRegistry<>(SparrowEvent.class);
        this.eventBus = new SimpleEventBus<>(registry, new EventBus.EventExceptionHandler() {
            @Override
            public <E> void eventExceptionCaught(EventBus<? super E> bus, EventSubscription<? super E> subscription, E event, Throwable throwable) {
                plugin.getBootstrap().getPluginLogger().severe("Exception caught in event handler", throwable);
            }
        });
    }

    public <T extends SparrowEvent> EventSubscription<T> subscribe(final Class<T> event, final EventSubscriber<? super T> subscriber) {
        return registry.subscribe(event, subscriber);
    }

    public EventBus<?> getEventBus() {
        return this.eventBus;
    }

    public void dispatch(SparrowEvent event) {
        this.eventBus.post(event, OptionalInt.empty());
    }
}
