package net.momirealms.sparrow.common.event;

import net.momirealms.sparrow.common.event.bus.EventBus;
import net.momirealms.sparrow.common.event.bus.SimpleEventBus;
import net.momirealms.sparrow.common.event.gen.EventGenerator;
import net.momirealms.sparrow.common.event.registry.EventRegistry;
import net.momirealms.sparrow.common.event.registry.SimpleEventRegistry;
import net.momirealms.sparrow.common.config.plugin.SparrowPlugin;

import java.util.OptionalInt;

public class EventManagerImpl implements EventManager {

    private final EventBus<SparrowEvent> eventBus;
    private final EventRegistry<SparrowEvent> registry;
    private final SparrowPlugin plugin;

    protected EventManagerImpl(SparrowPlugin plugin) {
        this.plugin = plugin;
        this.registry = new SimpleEventRegistry<>(SparrowEvent.class);
        this.eventBus = new SimpleEventBus<>(registry, new EventBus.EventExceptionHandler() {
            @Override
            public <E> void eventExceptionCaught(EventBus<? super E> bus, EventSubscription<? super E> subscription, E event, Throwable throwable) {
                plugin.getBootstrap().getPluginLogger().severe("Exception caught in event handler", throwable);
            }
        });
    }

    @Override
    public <T extends SparrowEvent> EventSubscription<T> subscribe(final Class<T> event, final EventSubscriber<? super T> subscriber) {
        return registry.subscribe(event, subscriber);
    }

    @Override
    public <T extends SparrowEvent> EventSubscription<T> subscribe(final Class<T> event, EventConfig config, final EventSubscriber<? super T> subscriber) {
        return registry.subscribe(event, config, subscriber);
    }

    @Override
    public void dispatch(Class<? extends SparrowEvent> eventClass, Object... params) {
        SparrowEvent event = generate(eventClass, params);
        this.eventBus.post(event, OptionalInt.empty());
    }

    @Override
    public void dispatch(Class<? extends SparrowEvent> eventClass, OptionalInt order, Object... params) {
        SparrowEvent event = generate(eventClass, params);
        this.eventBus.post(event, order);
    }

    @Override
    public EventBus<?> getEventBus() {
        return this.eventBus;
    }

    private SparrowEvent generate(Class<? extends SparrowEvent> eventClass, Object... params) {
        try {
            return EventGenerator.generate(eventClass).newInstance(this.plugin, params);
        } catch (Throwable e) {
            throw new RuntimeException("Exception occurred whilst generating event instance", e);
        }
    }
}
