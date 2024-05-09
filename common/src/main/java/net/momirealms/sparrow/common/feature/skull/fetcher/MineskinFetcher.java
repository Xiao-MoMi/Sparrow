package net.momirealms.sparrow.common.feature.skull.fetcher;

import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.feature.skull.SkullFetcher;
import org.mineskin.MineskinClient;
import org.mineskin.SkinOptions;
import org.mineskin.data.Skin;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MineskinFetcher implements SkullFetcher {
    private final URL url;
    private final MineskinClient client;

    public MineskinFetcher(URL url) {
        this.url = url;
        this.client = new MineskinClient("Sparrow-Plugin");
    }

    @Override
    public CompletableFuture<SkullData> fetchData() {
        CompletableFuture<Skin> skinFuture = client.generateUrl(url.toString(), SkinOptions.none());
        return skinFuture.thenApply(skin -> SkullData.builder()
                .owner(skin.name)
                .uuid(UUID.fromString(skin.uuid))
                .textureBase64(skin.data.texture.value)
                .build()
        );
    }
}
