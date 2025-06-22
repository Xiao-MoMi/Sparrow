package net.momirealms.sparrow.bukkit.feature.item.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saicone.rtag.RtagItem;
import com.saicone.rtag.data.ComponentType;
import com.saicone.rtag.tag.TagList;
import com.saicone.rtag.util.ChatComponent;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.item.ComponentKeys;
import net.momirealms.sparrow.common.helper.GsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ComponentItemFactory1_21_5 extends ComponentItemFactory1_21_4 {

    public ComponentItemFactory1_21_5(SparrowBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void displayName(RtagItem item, String json) {
        item.setComponent(ComponentKeys.CUSTOM_NAME, ChatComponent.toTag(ChatComponent.fromJson(json)));
    }

    @Override
    protected Optional<String> displayName(RtagItem item) {
        if (!item.hasComponent(ComponentKeys.CUSTOM_MODEL_DATA)) return Optional.empty();
        Optional<Object> optionalNBT = ComponentType.encodeNbt(
                ComponentKeys.CUSTOM_NAME,
                item.getComponent(ComponentKeys.CUSTOM_NAME)
        );
        return optionalNBT.map(o -> ChatComponent.toJson(ChatComponent.fromTag(o)));
    }

    @Override
    protected Optional<List<String>> lore(RtagItem item) {
        if (item.getComponent(ComponentKeys.LORE) == null) return Optional.empty();
        Optional<JsonElement> optionalJson = ComponentType.encodeJson(
                ComponentKeys.CUSTOM_NAME,
                item.getComponent(ComponentKeys.CUSTOM_NAME)
        );
        if (optionalJson.isEmpty()) return Optional.empty();
        JsonElement element = optionalJson.get();
        if (element instanceof JsonObject jsonObject) {
            if (jsonObject.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(List.of(GsonHelper.get().toJson(jsonObject)));
        } else if (element instanceof JsonArray jsonArray) {
            List<String> lore = new ArrayList<>();
            for (JsonElement jsonElement : jsonArray) {
                lore.add(GsonHelper.get().toJson(jsonElement));
            }
            return Optional.of(lore);
        } else {
            return Optional.empty();
        }
    }

    @Override
    protected void lore(RtagItem item, List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            item.removeComponent(ComponentKeys.LORE);
        } else {
            List<Object> tags = new ArrayList<>(lore.size());
            for (String s : lore) {
                tags.add(ChatComponent.toTag(ChatComponent.fromJson(s)));
            }
            item.setComponent(ComponentKeys.LORE, TagList.newTag(tags));
        }
    }
}