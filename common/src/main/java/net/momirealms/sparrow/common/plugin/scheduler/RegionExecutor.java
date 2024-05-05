package net.momirealms.sparrow.common.plugin.scheduler;

public interface RegionExecutor<T> {

    void execute(Runnable r, T l);
}
