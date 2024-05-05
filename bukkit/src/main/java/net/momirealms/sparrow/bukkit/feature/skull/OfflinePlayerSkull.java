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

public final class OfflinePlayerSkull implements Skull {
    private final CompletableFuture<String> base64;

    OfflinePlayerSkull(String name) {
        this(name, 5, TimeUnit.SECONDS);
    }
    
    OfflinePlayerSkull(String name, int timeout, TimeUnit timeUnit) {
        this.base64 = CompletableFuture.supplyAsync(() -> {
                    try {
                        return fetchSkinBase64(name);
                    } catch (IOException e) {
                        throw new FailedToGetSkullException(name, e);
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
    private String fetchSkinBase64(String name) throws IOException {
        // Use Mojang API to get the skin texture
        try {
            URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + name);
            JsonObject jsonObject = JsonParser.parseString(NetWorkUtils.getUrlResponse(url)).getAsJsonObject();
            JsonObject textures = jsonObject.getAsJsonObject("textures");
            JsonObject raw = textures.getAsJsonObject("raw");

            return raw.get("value").getAsString();
        } catch (Exception e) {
            // If the API fails, use the profile API to get the skin texture
            final URL getUniqueIdUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            final JsonObject jsonObject = JsonParser.parseString(NetWorkUtils.getUrlResponse(getUniqueIdUrl)).getAsJsonObject();
            final UUID uniqueId = UUID.fromString(jsonObject.get("id").getAsString());

            final URL texturesUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uniqueId + "?unsigned=false");
            final JsonObject textures = JsonParser.parseString(NetWorkUtils.getUrlResponse(texturesUrl)).getAsJsonObject();
            final JsonObject properties = textures.getAsJsonArray("properties").get(0).getAsJsonObject();
            return properties.get("value").getAsString();
        }

    }
}
