package net.momirealms.sparrow.common.helper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.option.OptionState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdventureHelper {

    private static AdventureHelper helper;
    private final MiniMessage miniMessage;
    private final MiniMessage miniMessageStrict;
    private final LegacyComponentSerializer legacyComponentSerializer;
    private final GsonComponentSerializer gsonComponentSerializer;

    private AdventureHelper() {
        this.miniMessage = MiniMessage.builder().build();
        this.miniMessageStrict = MiniMessage.builder().strict(true).build();
        this.legacyComponentSerializer = LegacyComponentSerializer.builder()
                .hexColors()
                .character('§')
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
        this.gsonComponentSerializer = GsonComponentSerializer.builder().build();
    }

    private static AdventureHelper getInstance() {
        if (helper == null) {
            helper = new AdventureHelper();
        }
        return helper;
    }

    public static MiniMessage getMiniMessage() {
        return getInstance().miniMessage;
    }

    public static MiniMessage getStrictMiniMessage() {
        return getInstance().miniMessageStrict;
    }

    public static GsonComponentSerializer getGson() {
        return getInstance().gsonComponentSerializer;
    }

    public static String legacyToMiniMessage(String legacy) {
        TextComponent component = getInstance().legacyComponentSerializer.deserialize(legacy.replace("&", "§"));
        String modern = getStrictMiniMessage().serialize(component);
        // TODO Fix this unsafe method
        modern = modern.replace("\\<", "<");
        return modern;
    }

    public static String componentToJson(Component component) {
        return getGson().serialize(component);
    }
}
