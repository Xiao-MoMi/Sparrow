package net.momirealms.sparrow.bukkit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.entity.Player;

public class BukkitPacketManager {

    private SparrowBukkitPlugin plugin;

    public BukkitPacketManager(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin.getBootstrap().getLoader()));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    public void init() {
        PacketEvents.getAPI().init();
    }

    public void terminate() {
        PacketEvents.getAPI().terminate();
    }

    public void sendPacket(Player player, PacketWrapper<?>... packetWrapper) {
        for (PacketWrapper<?> wrapper : packetWrapper) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);
        }
    }
}
