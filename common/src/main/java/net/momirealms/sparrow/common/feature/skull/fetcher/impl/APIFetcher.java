package net.momirealms.sparrow.common.feature.skull.fetcher.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.feature.skull.SkullFetchException;
import net.momirealms.sparrow.common.feature.skull.fetcher.SkullFetcher;
import net.momirealms.sparrow.common.helper.GsonHelper;
import net.momirealms.sparrow.common.util.Commons;
import net.momirealms.sparrow.common.util.Either;
import net.momirealms.sparrow.common.util.ThrowableFunction;
import net.momirealms.sparrow.common.util.UUIDUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class APIFetcher implements SkullFetcher {

    private static final List<ThrowableFunction<Either<String, UUID>, SkullData, SkullFetchException>> onlineAPIs = new ArrayList<>(
            List.of(
                    APIFetcher::fetchDataFromMojang,
                    APIFetcher::fetchDataFromAshcon
            )
    );
    private final CompletableFuture<SkullData> futureSkull;

    public APIFetcher(Either<String, UUID> either) {
        this.futureSkull = CompletableFuture.supplyAsync(() -> fetchSkullData(either));
    }

    private static SkullData fetchSkullData(Either<String, UUID> either) {
        var ex = new SkullFetchException[1];
        for (int i = 0; i < onlineAPIs.size(); i++) {
            var fetcher = onlineAPIs.get(i);
            try {
                return fetcher.apply(either);
            } catch (SkullFetchException e) {
                if (i == 0 && e.getReason() == SkullFetchException.Reason.UNKNOWN) {
                    ex[0] = e;
                    break;
                }
                if (i == onlineAPIs.size() - 1) {
                    ex[0] = e;
                }
            } catch (RuntimeException e) {
                if (i == onlineAPIs.size() - 1) {
                    ex[0] = new SkullFetchException(SkullFetchException.Reason.UNKNOWN, "Unknown exception occurred when fetching skins", e);
                }
            }
        }
        if (ex[0] != null) {
            return null;
        }
        return null;
    }

    @Override
    public CompletableFuture<SkullData> fetchData() {
        return futureSkull;
    }

    private static SkullData fetchDataFromMojang(Either<String, UUID> either) throws SkullFetchException {
        UUID uuid = either.fallbackOrMapPrimary(name -> {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                return switch (connection.getResponseCode()) {
                    case 429 ->
                            throw new SkullFetchException(SkullFetchException.Reason.THROTTLED, Commons.readInput(connection.getErrorStream()));
                    case 204, 404 ->
                            null;
                    case 200 -> {
                        JsonObject data = GsonHelper.get().fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);
                        boolean demo = data.get("demo") != null;
                        if (demo) {
                            yield null;
                        }
                        yield UUIDUtils.fromUnDashedUUID(data.getAsJsonPrimitive("id").getAsString());
                    }
                    case 500 ->
                            throw new SkullFetchException(SkullFetchException.Reason.INTERNAL, Commons.readInput(connection.getErrorStream()));
                    default ->
                            throw new SkullFetchException(SkullFetchException.Reason.UNKNOWN, Commons.readInput(connection.getErrorStream()));
                };
            } catch (IOException e) {
                throw new SkullFetchException(SkullFetchException.Reason.INTERNAL, String.format("Failed to get %s's skin", name), e);
            }
        });

        // Not a premium user
        if (uuid == null) {
            return null;
        }

        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDUtils.toUnDashedUUID(uuid));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            switch (connection.getResponseCode()) {
                case 200 -> {
                    JsonObject data = GsonHelper.get().fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);
                    String username = data.getAsJsonPrimitive("name").getAsString();
                    JsonArray array = data.getAsJsonArray("properties");
                    for (JsonElement element : array.asList()) {
                        if (element.isJsonObject()) {
                            JsonObject jsonObject = (JsonObject) element;
                            JsonPrimitive propertyName = jsonObject.getAsJsonPrimitive("name");
                            if (propertyName != null && propertyName.getAsString().equals("textures")) {
                                String base64 = jsonObject.getAsJsonPrimitive("value").getAsString();
                                return SkullData.builder()
                                        .owner(username)
                                        .uuid(uuid)
                                        .textureBase64(base64)
                                        .build();
                            }
                        }
                    }
                    return null;
                }
                case 404 -> {
                    return null;
                }
                case 429 ->
                        throw new SkullFetchException(SkullFetchException.Reason.THROTTLED, Commons.readInput(connection.getErrorStream()));
                default ->
                        throw new SkullFetchException(SkullFetchException.Reason.UNKNOWN, Commons.readInput(connection.getErrorStream()));
            }
        } catch (IOException e) {
            throw new SkullFetchException(SkullFetchException.Reason.INTERNAL, String.format("Failed to get %s's skin", uuid.toString()), e);
        }
    }

    private static SkullData fetchDataFromAshcon(Either<String, UUID> either) throws SkullFetchException {
        String nameOrUUID = either.primaryOrMapFallback(UUID::toString);
        try {
            URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + nameOrUUID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            switch (connection.getResponseCode()) {
                case 200 -> {
                    JsonObject data = GsonHelper.get().fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);
                    UUID uuid = UUID.fromString(data.getAsJsonPrimitive("uuid").getAsString());
                    String username = data.getAsJsonPrimitive("username").getAsString();
                    JsonObject textures = data.getAsJsonObject("textures");
                    String textureBase64 = textures.getAsJsonPrimitive("raw").getAsString();
                    return SkullData.builder()
                            .owner(username)
                            .uuid(uuid)
                            .textureBase64(textureBase64)
                            .build();
                }
                case 404 -> {
                    return null;
                }
                case 429 ->
                        throw new SkullFetchException(SkullFetchException.Reason.THROTTLED, Commons.readInput(connection.getErrorStream()));
                default ->
                        throw new SkullFetchException(SkullFetchException.Reason.UNKNOWN, Commons.readInput(connection.getErrorStream()));
            }
        } catch (IOException e) {
            throw new SkullFetchException(SkullFetchException.Reason.INTERNAL, String.format("Failed to get %s's skin", nameOrUUID), e);
        }
    }
}
