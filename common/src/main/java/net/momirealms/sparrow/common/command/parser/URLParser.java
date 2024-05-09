package net.momirealms.sparrow.common.command.parser;

import net.momirealms.sparrow.common.locale.CaptionConstants;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class URLParser<C> implements ArgumentParser<C, URL>,
        BlockingSuggestionProvider.Strings<C> {

    public static <C> @NotNull ParserDescriptor<C, URL> urlParser() {
        return ParserDescriptor.of(new URLParser<>(), URL.class);
    }

    public static <C> CommandComponent.@NonNull Builder<C, URL> urlComponent() {
        return CommandComponent.<C, URL>builder().parser(urlParser());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull URL> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        final String input = commandInput.peekString();

        final URL url;
        try {
            url = new URI(input).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            return ArgumentParseResult.failure(new URIParseException(input, commandContext));
        }

        commandInput.readString();
        return ArgumentParseResult.success(url);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        if (input.isEmpty(true)) {
            return List.of("http://", "https://", "ftp://", "file://");
        }

        return Collections.emptyList();
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
                    CaptionConstants.ARGUMENT_PARSE_FAILURE_TIME,
                    CaptionVariable.of(input, "input")
            );
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}
