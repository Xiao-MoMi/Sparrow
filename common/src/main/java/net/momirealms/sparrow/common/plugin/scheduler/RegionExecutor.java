package net.momirealms.sparrow.common.plugin.scheduler;

import net.momirealms.sparrow.common.util.Location;

public interface RegionExecutor {

    void execute(Runnable r, Location l);
}
