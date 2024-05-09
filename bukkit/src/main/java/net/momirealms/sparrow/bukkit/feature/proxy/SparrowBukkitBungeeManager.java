package net.momirealms.sparrow.bukkit.feature.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class SparrowBukkitBungeeManager implements PluginMessageListener, Listener {

    private final static String GET_SERVERS_PERM = "sparrow.proxy.getservers";
    private final SparrowBukkitPlugin plugin;
    private final HashMap<String, Consumer<ByteArrayDataInput>> messageConsumers = new HashMap<>();
    private final ArrayList<String> backendServers = new ArrayList<>();
    private boolean proxyEnabled = false;

    public SparrowBukkitBungeeManager(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        this.proxyEnabled = plugin.getLoader().getServer().spigot().getSpigotConfig().getBoolean("settings.bungeecord");
        if (!this.proxyEnabled) {
            try {
                this.proxyEnabled = plugin.getLoader().getServer().spigot().getPaperConfig().getBoolean("proxies.velocity.enabled");
            } catch (Exception ignored) {
            }
        }

        if (!this.proxyEnabled) {
            return;
        }

        this.plugin.getLoader()
                .getServer()
                .getMessenger()
                .registerOutgoingPluginChannel(plugin.getLoader(), "BungeeCord");
        this.plugin.getLoader()
                .getServer()
                .getMessenger()
                .registerIncomingPluginChannel(plugin.getLoader(), "BungeeCord", this);
        this.plugin.getLoader()
                .getServer()
                .getPluginManager()
                .registerEvents(this, plugin.getLoader());
        this.registerConsumers();
    }

    public void disable() {
        if (!this.proxyEnabled) {
            return;
        }

        this.plugin.getLoader()
                .getServer()
                .getMessenger().unregisterOutgoingPluginChannel(plugin.getLoader(), "BungeeCord");
        this.plugin.getLoader()
                .getServer()
                .getMessenger().unregisterIncomingPluginChannel(plugin.getLoader(), "BungeeCord", this);
        HandlerList.unregisterAll(this);
    }

    private void registerConsumers() {
        this.messageConsumers.put(BungeeCordChannels.GET_SERVERS, in -> {
            this.backendServers.clear();
            String[] serverList = in.readUTF().split(", ");
            this.backendServers.addAll(Arrays.asList(serverList));
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission(GET_SERVERS_PERM)) {
            sendGetServersRequest();
        }
    }

    public void connectServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(BungeeCordChannels.CONNECT_OTHER);
        out.writeUTF(player.getName());
        out.writeUTF(server);
        player.sendPluginMessage(plugin.getLoader(), BungeeCordChannels.MAIN, out.toByteArray());
    }

    public void sendGetServersRequest() {
        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(
                player -> {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF(BungeeCordChannels.GET_SERVERS);
                    player.sendPluginMessage(plugin.getLoader(), BungeeCordChannels.MAIN, out.toByteArray());
                }
        );
    }

    public Collection<String> getBackendServers() {
        return new ArrayList<>(backendServers);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals(BungeeCordChannels.MAIN)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        Optional.ofNullable(messageConsumers.get(subChannel))
                .ifPresent(consumer -> consumer.accept(in));
    }

    public static class BungeeCordChannels {

        public static final String MAIN = "BungeeCord";
        public static final String GET_SERVERS = "GetServers";
        public static final String CONNECT_OTHER = "ConnectOther";
    }
}
