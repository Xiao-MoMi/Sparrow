package net.momirealms.sparrow.common.event;

import net.momirealms.sparrow.common.event.bus.EventBus;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;

import java.util.OptionalInt;

public interface EventManager {

    class SingletonHolder {
        private static EventManager INSTANCE = null;
    }

    static EventManager create(SparrowPlugin plugin) {
        if (SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new EventManagerImpl(plugin);
        }
        return SingletonHolder.INSTANCE;
    }

    <T extends SparrowEvent> EventSubscription<T> subscribe(Class<T> event, EventSubscriber<? super T> subscriber);

    <T extends SparrowEvent> EventSubscription<T> subscribe(Class<T> event, EventConfig config, EventSubscriber<? super T> subscriber);

    SparrowEvent dispatch(Class<? extends SparrowEvent> eventClass, Object... params);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    SparrowEvent dispatch(Class<? extends SparrowEvent> eventClass, OptionalInt order, Object... params);

    EventBus<?> getEventBus();
}
