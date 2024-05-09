package net.momirealms.sparrow.common.feature.skull.fetcher;

import net.momirealms.sparrow.common.feature.skull.SkullData;

import java.util.concurrent.CompletableFuture;

public interface SkullFetcher {

    CompletableFuture<SkullData> fetchData();
}
