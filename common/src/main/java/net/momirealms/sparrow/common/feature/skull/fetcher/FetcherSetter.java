package net.momirealms.sparrow.common.feature.skull.fetcher;

import net.momirealms.sparrow.common.feature.skull.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.argument.NameSkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.URLSkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.UUIDSkullArgument;
import net.momirealms.sparrow.common.util.Either;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class FetcherSetter {
    private @Nullable FetcherSetter next;

    protected SkullArgument argument;

    protected FetcherSetter(SkullArgument argument) {
        this.argument = argument;
    }

    @NotNull
    public static FetcherSetter chain(@NotNull FetcherSetter head, @NotNull FetcherSetter... setters) {
        FetcherSetter current = head;
        for (FetcherSetter setter : setters) {
            current.next = setter;
            current = setter;
        }
        return head;
    }

    @NotNull
    public SkullFetcher fetcher() {
        SkullFetcher fetcher;
        try {
            fetcher = getFetcher();
        } catch (Exception e) {
            if (next != null) {
                return next.fetcher();
            }
            throw new IllegalArgumentException("Invalid skull argument" + argument.toString(), e);
        }
        return fetcher;
    }

    @NotNull
    protected abstract SkullFetcher getFetcher() throws RuntimeException;

    public static class Mineskin extends FetcherSetter {
        public Mineskin(SkullArgument argument) {
            super(argument);
        }

        @NotNull
        @Override
        protected SkullFetcher getFetcher() throws RuntimeException {
            URLSkullArgument urlSkullArgument = (URLSkullArgument) argument;
            return new MineskinFetcher(urlSkullArgument.url());
        }
    }

    public static class API extends FetcherSetter {
        public API(SkullArgument argument) {
            super(argument);
        }

        @NotNull
        @Override
        protected SkullFetcher getFetcher() throws RuntimeException {
            Either<String, UUID> either;
            if (argument instanceof NameSkullArgument nameSkullArgument) {
                either = Either.ofPrimary(nameSkullArgument.name());
            } else if (argument instanceof UUIDSkullArgument uuidSkullArgument) {
                either = Either.ofFallback(uuidSkullArgument.uniqueId());
            } else {
                throw new IllegalArgumentException("Invalid skull argument" + argument.toString());
            }

            return new APIFetcher(either);
        }
    }
}
