package net.momirealms.sparrow.bukkit.feature.item.impl;

import com.saicone.rtag.RtagItem;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.feature.item.SparrowBukkitItemFactory;
import net.momirealms.sparrow.common.feature.skull.SkullData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UniversalItemFactory extends SparrowBukkitItemFactory {

    public UniversalItemFactory(SparrowBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void displayName(RtagItem item, String json) {
        if (json != null) {
            item.set(json, "display", "Name");
        } else {
            item.remove("display", "Name");
        }
    }

    @Override
    protected Optional<String> displayName(RtagItem item) {
        if (!item.hasTag("display", "Name")) return Optional.empty();
        return Optional.of(item.get("display", "Name"));
    }

    @Override
    protected void customModelData(RtagItem item, Integer data) {
        if (data == null) {
            item.remove("CustomModelData");
        } else {
            item.set(data, "CustomModelData");
        }
    }

    @Override
    protected Optional<Integer> customModelData(RtagItem item) {
        if (!item.hasTag("CustomModelData")) return Optional.empty();
        return Optional.of(item.get("CustomModelData"));
    }

    @Override
    protected void skull(RtagItem item, SkullData skullData) {
        if (skullData == null) {
            item.remove("SkullOwner");
        } else {
            item.set(skullData.getUUID(), "SkullOwner", "Id");
            item.set(skullData.getOwner(), "SkullOwner", "Name");
            item.set(List.of(Map.of("Value", skullData.getTextureBase64())), "SkullOwner", "Properties", "textures");
        }
    }

    @Override
    protected Optional<List<String>> lore(RtagItem item) {
        if (!item.hasTag("display", "Lore")) return Optional.empty();
        return Optional.of(item.get("display", "Lore"));
    }

    @Override
    protected void lore(RtagItem item, List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            item.remove("display", "Lore");
        } else {
            item.set(lore, "display", "Lore");
        }
    }
}
