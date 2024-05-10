package net.momirealms.sparrow.common.config.plugin.scheduler;

public interface RegionExecutor<T> {

    void execute(Runnable r, T l);
}
