package net.momirealms.sparrow.common.feature.skull.fetcher.provider;

import net.momirealms.sparrow.common.feature.skull.argument.NameSkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.UUIDSkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.fetcher.impl.APIFetcher;
import net.momirealms.sparrow.common.util.Either;

import java.util.Optional;

public class APIFetcherProvider implements FetcherProvider {

    @Override
    public Optional<SkullFetcher> getOrEmpty(SkullArgument skullArgument) {
        if (skullArgument instanceof NameSkullArgument nameSkullArgument) {
            return Optional.of(new APIFetcher(Either.ofPrimary(nameSkullArgument.name())));
        } else if (skullArgument instanceof UUIDSkullArgument uuidSkullArgument) {
            return Optional.of(new APIFetcher(Either.ofFallback(uuidSkullArgument.uniqueId())));
        }
        return Optional.empty();
    }
}
