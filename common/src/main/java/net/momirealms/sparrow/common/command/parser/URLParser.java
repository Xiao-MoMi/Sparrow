package net.momirealms.sparrow.common.command.parser;

import net.momirealms.sparrow.common.locale.SparrowCaptionKeys;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.MappedArgumentParser;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class URLParser<C> implements MappedArgumentParser.Mapper<C, String, URL>{

    @Override
    public @NonNull CompletableFuture<ArgumentParseResult<URL>> map(@NonNull CommandContext<C> context, @NonNull ArgumentParseResult<String> input) {
        if (input.failure().isPresent()) {
            return ArgumentParseResult.failureFuture(input.failure().get());
        }

        final String inputString = input.parsedValue().orElse("");

        final URL url;
        try {
            url = URI.create(inputString).toURL();
        } catch (Exception e) {
            return ArgumentParseResult.failureFuture(new URIParseException(inputString, context));
        }

        return ArgumentParseResult.successFuture(url);
    }

    public static final class URIParseException extends ParserException {

        private final String input;

        public URIParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    URLParser.class,
                    context,
                    SparrowCaptionKeys.ARGUMENT_PARSE_FAILURE_URL,
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}
