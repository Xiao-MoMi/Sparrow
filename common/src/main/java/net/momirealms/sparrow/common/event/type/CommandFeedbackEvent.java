package net.momirealms.sparrow.common.event.type;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.common.event.Cancellable;
import net.momirealms.sparrow.common.event.Param;
import net.momirealms.sparrow.common.event.SparrowEvent;

public interface CommandFeedbackEvent<C> extends SparrowEvent, Cancellable {

    @Param(0)
    C sender();

    @Param(1)
    String key();

    @Param(2)
    Component message();
}
