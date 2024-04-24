package net.momirealms.sparrow.common.plugin.bootstrap;

public interface BootstrappedWithLoader {

    /**
     * Gets the loader object that did the bootstrapping.
     *
     * @return the loader
     */
    Object getLoader();

}