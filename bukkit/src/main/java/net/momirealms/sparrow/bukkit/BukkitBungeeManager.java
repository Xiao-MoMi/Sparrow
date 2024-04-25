package net.momirealms.sparrow.bukkit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

public class BukkitBungeeManager {

    private final SparrowBukkitPlugin plugin;

    public BukkitBungeeManager(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        plugin.getLoader()
                .getServer()
                .getMessenger()
                .registerOutgoingPluginChannel(plugin.getLoader(), "BungeeCord");
    }

    public void disable() {
        plugin.getLoader()
                .getServer()
                .getMessenger().unregisterOutgoingPluginChannel(plugin.getLoader(), "BungeeCord");
    }

    public void connectServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getName());
        out.writeUTF(server);
        player.sendPluginMessage(plugin.getLoader(), "BungeeCord", out.toByteArray());
    }
}
