/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.common.config.plugin.Platform;
import net.momirealms.sparrow.common.config.plugin.bootstrap.BootstrappedWithLoader;
import net.momirealms.sparrow.common.config.plugin.bootstrap.SparrowBootstrap;
import net.momirealms.sparrow.common.config.plugin.classpath.ClassPathAppender;
import net.momirealms.sparrow.common.config.plugin.classpath.JarInJarClassPathAppender;
import net.momirealms.sparrow.common.config.plugin.logging.JavaPluginLogger;
import net.momirealms.sparrow.common.config.plugin.logging.PluginLogger;
import net.momirealms.sparrow.common.config.plugin.scheduler.SchedulerAdapter;
import net.momirealms.sparrow.loader.LoaderBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Bootstrap plugin for Sparrow running on Bukkit.
 */
public class SparrowBukkitBootstrap implements SparrowBootstrap, LoaderBootstrap, BootstrappedWithLoader {

    private final JavaPlugin loader;

    /**
     * The plugin logger
     */
    private final PluginLogger logger;

    /**
     * The plugin class path appender
     */
    private final ClassPathAppender classPathAppender;

    /**
     * The plugin instance
     */
    private final SparrowBukkitPlugin plugin;

    /**
     * The time when the plugin was enabled
     */
    private Instant startTime;
    /**
     * A scheduler adapter for the platform
     */
    private final SparrowBukkitSchedulerAdapter schedulerAdapter;

    // load/enable latches
    private final CountDownLatch loadLatch = new CountDownLatch(1);
    private final CountDownLatch enableLatch = new CountDownLatch(1);
    private boolean serverStarting = true;
    private boolean serverStopping = false;

    public SparrowBukkitBootstrap(JavaPlugin loader) {
        this.loader = loader;
        this.logger = new JavaPluginLogger(loader.getLogger());
        this.classPathAppender = new JarInJarClassPathAppender(getClass().getClassLoader());
        this.plugin = new SparrowBukkitPlugin(this);
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            folia = true;
        } catch (ClassNotFoundException ignored) {
        }

        this.schedulerAdapter = new SparrowBukkitSchedulerAdapter(this,
            folia ?
                (r, l) -> {
                    if (l == null) this.getServer().getGlobalRegionScheduler().execute(this.getLoader(), r);
                    else this.getServer().getRegionScheduler().execute(this.getLoader(), l, r);
                } :
                (r, l) -> this.getServer().getScheduler().scheduleSyncDelayedTask(this.getLoader(), r)
        );
    }

    @Override
    public JavaPlugin getLoader() {
        return this.loader;
    }

    public Server getServer() {
        return this.loader.getServer();
    }

    @Override
    public PluginLogger getPluginLogger() {
        return this.logger;
    }

    @Override
    public ClassPathAppender getClassPathAppender() {
        return this.classPathAppender;
    }

    @Override
    public void onLoad() {
        try {
            this.plugin.load();
        } finally {
            this.loadLatch.countDown();
        }
    }

    @Override
    public void onEnable() {
        this.serverStarting = true;
        this.serverStopping = false;
        this.startTime = Instant.now();
        try {
            this.plugin.enable();
            this.plugin.reload();
            getServer().getScheduler().runTask(this.loader, () -> this.serverStarting = false);
        } finally {
            this.enableLatch.countDown();
        }
    }

    @Override
    public void onDisable() {
        this.serverStopping = true;
        this.plugin.disable();
    }

    @Override
    public CountDownLatch getEnableLatch() {
        return this.enableLatch;
    }

    @Override
    public CountDownLatch getLoadLatch() {
        return this.loadLatch;
    }

    public boolean isServerStarting() {
        return this.serverStarting;
    }

    public boolean isServerStopping() {
        return this.serverStopping;
    }

    @Override
    public String getVersion() {
        return this.loader.getDescription().getVersion();
    }

    @Override
    public SchedulerAdapter<Location> getScheduler() {
        return schedulerAdapter;
    }

    @Override
    public Instant getStartupTime() {
        return this.startTime;
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUKKIT;
    }

    @Override
    public String getServerBrand() {
        return getServer().getName();
    }

    @Override
    public String getServerVersion() {
        return Bukkit.getServer().getBukkitVersion().split("-")[0];
    }

    @Override
    public Path getDataDirectory() {
        return this.loader.getDataFolder().toPath().toAbsolutePath();
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return Optional.ofNullable(getServer().getPlayer(uniqueId));
    }

    @Override
    public Optional<UUID> lookupUniqueId(String username) {
        return Optional.of(getServer().getOfflinePlayer(username)).map(OfflinePlayer::getUniqueId);
    }

    @Override
    public Optional<String> lookupUsername(UUID uniqueId) {
        return Optional.of(getServer().getOfflinePlayer(uniqueId)).map(OfflinePlayer::getName);
    }

    @Override
    public int getPlayerCount() {
        return getServer().getOnlinePlayers().size();
    }

    @Override
    public Collection<String> getPlayerList() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        List<String> list = new ArrayList<>(players.size());
        for (Player player : players) {
            list.add(player.getName());
        }
        return list;
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        List<UUID> list = new ArrayList<>(players.size());
        for (Player player : players) {
            list.add(player.getUniqueId());
        }
        return list;
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        Player player = getServer().getPlayer(uniqueId);
        return player != null && player.isOnline();
    }

    @Override
    public @Nullable String identifyClassLoader(ClassLoader classLoader) throws ReflectiveOperationException {
        Class<?> pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
        if (pluginClassLoaderClass.isInstance(classLoader)) {
            Method getPluginMethod = pluginClassLoaderClass.getDeclaredMethod("getPlugin");
            getPluginMethod.setAccessible(true);

            JavaPlugin plugin = (JavaPlugin) getPluginMethod.invoke(classLoader);
            return plugin.getName();
        }
        return null;
    }
}
