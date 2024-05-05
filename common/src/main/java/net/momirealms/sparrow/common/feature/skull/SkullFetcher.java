package net.momirealms.sparrow.common.feature.skull;

import java.util.concurrent.CompletableFuture;

public interface SkullFetcher {

    CompletableFuture<SkullData> fetchData();
}
