package net.momirealms.sparrow.common.plugin.bootstrap;

import net.momirealms.sparrow.common.plugin.Platform;
import net.momirealms.sparrow.common.plugin.classpath.ClassPathAppender;
import net.momirealms.sparrow.common.plugin.logging.PluginLogger;
import net.momirealms.sparrow.common.plugin.scheduler.SchedulerAdapter;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public interface SparrowBootstrap {

    /**
     * Gets the plugin logger
     *
     * @return the logger
     */
    PluginLogger getPluginLogger();

    /**
     * Gets a {@link ClassPathAppender} for this bootstrap plugin
     *
     * @return a class path appender
     */
    ClassPathAppender getClassPathAppender();

    /**
     * Gets a string of the plugin's version
     *
     * @return the version of the plugin
     */
    String getVersion();

    /**
     * Gets an adapter for the platforms scheduler
     *
     * @return the scheduler
     */
    SchedulerAdapter getScheduler();

    Instant getStartupTime();

    Platform getPlatform();

    String getServerBrand();

    String getServerVersion();

    /**
     * Gets the plugins main data storage directory
     *
     * <p>Bukkit: ./plugins/Sparrow</p>
     * <p>BungeeCord: ./plugins/Sparrow</p>
     * <p>Velocity: ./plugins/Sparrow</p>
     *
     * @return the platforms data folder
     */
    Path getDataDirectory();

    /**
     * Gets the plugins configuration directory
     *
     * @return the config directory
     */
    default Path getConfigDirectory() {
        return getDataDirectory();
    }

    /**
     * Gets a bundled resource file from the jar
     *
     * @param path the path of the file
     * @return the file as an input stream
     */
    default InputStream getResourceStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    default URL getResource(String path) {
        return getClass().getClassLoader().getResource(path);
    }

    /**
     * Returns a countdown latch which {@link CountDownLatch#countDown() counts down}
     * after the plugin has loaded.
     *
     * @return a loading latch
     */
    CountDownLatch getLoadLatch();

    /**
     * Returns a countdown latch which {@link CountDownLatch#countDown() counts down}
     * after the plugin has enabled.
     *
     * @return an enable latch
     */
    CountDownLatch getEnableLatch();

    Optional<?> getPlayer(UUID uniqueId);

    Optional<UUID> lookupUniqueId(String username);

    Optional<String> lookupUsername(UUID uniqueId);

    int getPlayerCount();

    Collection<String> getPlayerList();

    Collection<UUID> getOnlinePlayers();

    boolean isPlayerOnline(UUID uniqueId);

    @Nullable
    String identifyClassLoader(ClassLoader classLoader) throws ReflectiveOperationException;
}
