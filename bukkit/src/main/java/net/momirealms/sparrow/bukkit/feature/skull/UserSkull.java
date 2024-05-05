package net.momirealms.sparrow.bukkit.feature.skull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.skull.FailedToGetSkullException;
import net.momirealms.sparrow.common.feature.skull.Skull;
import net.momirealms.sparrow.common.util.NetWorkUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class UserSkull implements Skull {
    private final CompletableFuture<String> base64;

    UserSkull(String name) {
        this(name, 5, TimeUnit.SECONDS);
    }
    
    UserSkull(String skinOrUniqueId, int timeout, TimeUnit timeUnit) {
        this.base64 = CompletableFuture.supplyAsync(() -> {
                    try {
                        return fetchSkinBase64(skinOrUniqueId);
                    } catch (IOException e) {
                        throw new FailedToGetSkullException(skinOrUniqueId, e);
                    }
                }, SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().async())
                .orTimeout(timeout, timeUnit);
    }

    @NotNull
    @Override
    public CompletableFuture<String> getBase64() {
        return base64;
    }

    @NotNull
    private String fetchSkinBase64(String skinOrUniqueId) throws IOException {
        // Use Mojang API to get the skin texture
        try {
            URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + skinOrUniqueId);
            JsonObject jsonObject = JsonParser.parseString(NetWorkUtils.getUrlResponse(url)).getAsJsonObject();
            JsonObject textures = jsonObject.getAsJsonObject("textures");
            JsonObject raw = textures.getAsJsonObject("raw");

            return raw.get("value").getAsString();
        } catch (Exception e) {
            // If the API fails, use the profile API to get the skin texture
            UUID uniqueId = UUID.fromString(skinOrUniqueId);
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uniqueId.toString().replace("-", ""));
            JsonObject jsonObject = JsonParser.parseString(NetWorkUtils.getUrlResponse(url)).getAsJsonObject();
            JsonObject properties = jsonObject.getAsJsonArray("properties").get(0).getAsJsonObject();

            return properties.get("value").getAsString();
        }

    }
}
