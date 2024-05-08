package net.momirealms.sparrow.bukkit.feature.enchant;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.common.feature.enchant.EnchantManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Optional;

public class SparrowBukkitEnchantManager implements Listener, EnchantManager<Player> {

    private final SparrowBukkitPlugin plugin;

    public SparrowBukkitEnchantManager(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin.getLoader());
    }

    public void disable() {
        HandlerList.unregisterAll(this);
    }

    public void reload() {
        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().registerEvents(this, plugin.getLoader());
    }

    @Override
    public void openEnchantmentTable(Player player, int shelves) {
        player.openEnchanting(null, true);
        if (shelves != 0) {
            player.setMetadata(EnchantManager.SHELVES_META_KEY, new FixedMetadataValue(SparrowBukkitPlugin.getInstance().getLoader(), shelves));
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onPreEnchant(PrepareItemEnchantEvent event) {
        final Player player = event.getEnchanter();
        if (!player.hasMetadata(SHELVES_META_KEY))
            return;
        List<MetadataValue> values = player.getMetadata(SHELVES_META_KEY);
        Optional<MetadataValue> value = values.stream().findAny();
        if (value.isEmpty())
            return;
        int shelves = value.get().asInt();
        var offers = event.getOffers();
        var newOffers = SparrowNMSProxy.getInstance().getEnchantmentOffers(player, event.getItem(), shelves);
        System.arraycopy(newOffers, 0, offers, 0, 3);
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.ENCHANTING)
            return;
        if (!event.getPlayer().hasMetadata(SHELVES_META_KEY))
            return;
        event.getPlayer().removeMetadata(SHELVES_META_KEY, plugin.getLoader());
    }
}
