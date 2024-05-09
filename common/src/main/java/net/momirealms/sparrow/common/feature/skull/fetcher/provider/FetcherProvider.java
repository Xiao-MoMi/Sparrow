package net.momirealms.sparrow.common.feature.skull.fetcher.provider;

import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.SkullFetcher;

import java.util.Optional;

public interface FetcherProvider {

    Optional<SkullFetcher> getOrEmpty(SkullArgument skullArgument);
}
