package net.momirealms.sparrow.common.event.type;

import net.momirealms.sparrow.common.event.Cancellable;
import net.momirealms.sparrow.common.event.Param;
import net.momirealms.sparrow.common.event.SparrowEvent;

public interface CommandFeedbackEvent extends SparrowEvent, Cancellable {

    @Param(0)
    String key();
}
