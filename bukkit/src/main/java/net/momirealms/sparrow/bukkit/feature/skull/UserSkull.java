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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class UserSkull implements Skull {
    private final CompletableFuture<String> base64;

    UserSkull(String name) {
        this(name, 5, TimeUnit.SECONDS);
    }
    
    UserSkull(String name, int timeout, TimeUnit timeUnit) {
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
        URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + name);
        JsonObject jsonObject = JsonParser.parseString(NetWorkUtils.getUrlResponse(url)).getAsJsonObject();
        JsonObject textures = jsonObject.getAsJsonObject("textures");
        JsonObject raw = textures.getAsJsonObject("raw");

        return raw.get("value").getAsString();
    }
}
