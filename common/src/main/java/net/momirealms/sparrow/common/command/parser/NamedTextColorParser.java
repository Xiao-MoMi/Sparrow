package net.momirealms.sparrow.common.command.parser;

import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.sparrow.common.locale.SparrowCaptionKeys;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.minecraft.extras.parser.TextColorParser;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.List;
import java.util.Locale;

public class NamedTextColorParser<C> implements ArgumentParser<C, NamedTextColor>,
        BlockingSuggestionProvider.Strings<C> {

    public static <C> @NonNull ParserDescriptor<C, NamedTextColor> namedTextColorParser() {
        return ParserDescriptor.of(new NamedTextColorParser<>(), NamedTextColor.class);
    }

    public static <C> CommandComponent.@NonNull Builder<C, NamedTextColor> namedTextColorComponent() {
        return CommandComponent.<C, NamedTextColor>builder().parser(namedTextColorParser());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull NamedTextColor> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        String color = commandInput.readString();
        NamedTextColor namedTextColor = NamedTextColor.NAMES.value(color.toLowerCase(Locale.ENGLISH));
        if (namedTextColor == null) {
            return ArgumentParseResult.failure(new NamedTextColorParseException(commandContext, color));
        }
        return ArgumentParseResult.success(namedTextColor);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return List.of("BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA",
                "DARK_RED", "DARK_PURPLE", "GOLD", "GRAY",
                "DARK_GRAY", "BLUE", "GREEN", "AQUA",
                "RED", "LIGHT_PURPLE", "YELLOW", "WHITE");
    }

    private static final class NamedTextColorParseException extends ParserException {


        private NamedTextColorParseException(
                final @NonNull CommandContext<?> commandContext,
                final @NonNull String input
        ) {
            super(
                    TextColorParser.class,
                    commandContext,
                    SparrowCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMEDTEXTCOLOR,
                    CaptionVariable.of("input", input)
            );
        }
    }
}
