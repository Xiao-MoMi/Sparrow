package net.momirealms.sparrow.common.feature.skull.fetcher.impl;

import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.feature.skull.fetcher.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.mineskin.MineskinClient;
import net.momirealms.sparrow.common.feature.skull.mineskin.SkinOptions;
import net.momirealms.sparrow.common.feature.skull.mineskin.Variant;
import net.momirealms.sparrow.common.feature.skull.mineskin.Visibility;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class MineskinFetcher implements SkullFetcher {

    private final CompletableFuture<SkullData> futureSkull;

    public MineskinFetcher(URL url) {
        this.futureSkull = fetchSkull(url);
    }

    @Override
    public CompletableFuture<SkullData> fetchData() {
        return futureSkull;
    }

    private static CompletableFuture<SkullData> fetchSkull(URL url) {
        MineskinClient client = new MineskinClient("anonymous");
        return client.generateUrl(url.toString(), SkinOptions.create("", Variant.AUTO, Visibility.PRIVATE))
                .thenApplyAsync(skin -> SkullData.builder()
                        .owner(skin.name)
                        .uuid(skin.data.uuid)
                        .textureBase64(skin.data.texture.value)
                        .build()
                );
    }
}
