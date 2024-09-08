package net.momirealms.sparrow.bukkit.loader;

import net.momirealms.sparrow.bukkit.SparrowBukkitBootstrap;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.loader.JarInJarClassLoader;
import net.momirealms.sparrow.loader.LoaderBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitLoaderPlugin extends JavaPlugin {

    private final LoaderBootstrap plugin;

    public BukkitLoaderPlugin() {
        plugin = new SparrowBukkitBootstrap(this);
    }

    @Override
    public void onLoad() {
        this.plugin.onLoad();
    }

    @Override
    public void onEnable() {
        this.plugin.onEnable();
    }

    @Override
    public void onDisable() {
        this.plugin.onDisable();
    }
}
