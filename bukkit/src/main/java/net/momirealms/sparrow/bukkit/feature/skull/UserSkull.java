package net.momirealms.sparrow.bukkit.feature.skull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.skull.Skull;
import net.momirealms.sparrow.common.util.NetWorkUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public final class UserSkull implements Skull {
    private final CompletableFuture<String> base64;

    UserSkull(String name) {
        this.base64 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchSkinBase64(name);
            } catch (IOException e) {
                return null;
            }
        }, SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().async());
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
