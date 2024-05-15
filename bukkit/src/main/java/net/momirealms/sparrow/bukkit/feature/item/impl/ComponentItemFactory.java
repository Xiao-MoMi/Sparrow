package net.momirealms.sparrow.bukkit.feature.item.impl;

import com.saicone.rtag.RtagItem;
import com.saicone.rtag.data.ComponentType;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.feature.item.SparrowBukkitItemFactory;
import net.momirealms.sparrow.common.feature.item.ComponentKeys;
import net.momirealms.sparrow.common.feature.skull.SkullData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ComponentItemFactory extends SparrowBukkitItemFactory {

    public ComponentItemFactory(SparrowBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void customModelData(RtagItem item, Integer data) {
        if (data == null) {
            item.removeComponent(ComponentKeys.CUSTOM_MODEL_DATA);
        } else {
            item.setComponent(ComponentKeys.CUSTOM_MODEL_DATA, data);
        }
    }

    @Override
    protected Optional<Integer> customModelData(RtagItem item) {
        if (!item.hasComponent(ComponentKeys.CUSTOM_MODEL_DATA)) return Optional.empty();
        return Optional.ofNullable(
                (Integer) ComponentType.encodeJava(
                        ComponentKeys.CUSTOM_MODEL_DATA,
                    item.getComponent(ComponentKeys.CUSTOM_MODEL_DATA)
                ).orElse(null)
        );
    }

    @Override
    protected void displayName(RtagItem item, String json) {
        if (json == null) {
            item.removeComponent(ComponentKeys.CUSTOM_NAME);
        } else {
            item.setComponent(ComponentKeys.CUSTOM_NAME, json);
        }
    }

    @Override
    protected Optional<String> displayName(RtagItem item) {
        if (item.getComponent(ComponentKeys.CUSTOM_NAME) == null) return Optional.empty();
        return Optional.ofNullable(
                (String) ComponentType.encodeJava(
                        ComponentKeys.CUSTOM_NAME,
                        item.getComponent(ComponentKeys.CUSTOM_NAME)
                ).orElse(null)
        );
    }

    @Override
    protected void skull(RtagItem item, SkullData skullData) {
        final Map<String, Object> profile = Map.of(
                "name", skullData.getOwner(),
                "id", skullData.getUUID(), // Maybe should be converted into 4-length int array
                "properties", List.of(
                        Map.of(
                                "name", "textures",
                                "value", skullData.getTextureBase64()
                        )
                )
        );
        item.setComponent("minecraft:profile", profile);
    }
}