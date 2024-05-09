package net.momirealms.sparrow.common.feature.skull.fetcher.provider;

import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.URLSkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.fetcher.impl.MineskinFetcher;

import java.util.Optional;

public class MineSkinFetcherProvider implements FetcherProvider {

    @Override
    public Optional<SkullFetcher> getOrEmpty(SkullArgument skullArgument) {
        if (skullArgument instanceof URLSkullArgument urlSkullArgument) {
            return Optional.of(new MineskinFetcher(urlSkullArgument.url()));
        }
        return Optional.empty();
    }
}
