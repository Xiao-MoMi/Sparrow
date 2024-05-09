package net.momirealms.sparrow.common.command.parser;

import net.momirealms.sparrow.common.locale.SparrowCaptionKeys;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TimeParser<C> implements ArgumentParser<C, Long>,
        BlockingSuggestionProvider.Strings<C> {

    private static final Pattern TIME_PATTERN = Pattern.compile("(([1-9][0-9]+|[1-9])[dhmst])");

    private static final HashMap<String, Integer> UNITS = new HashMap<>();

    public static <C> @NonNull ParserDescriptor<C, Long> timeParser() {
        return ParserDescriptor.of(new TimeParser<>(), Long.class);
    }

    public static <C> CommandComponent.@NonNull Builder<C, Long> timeComponent() {
        return CommandComponent.<C, Long>builder().parser(timeParser());
    }

    static {
        UNITS.put("d", 1728000);
        UNITS.put("h", 72000);
        UNITS.put("m", 1200);
        UNITS.put("s", 20);
        UNITS.put("t", 1);
        UNITS.put("", 1);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Long> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        final String input = commandInput.peekString();

        final Matcher matcher = TIME_PATTERN.matcher(input);

        long ticks = 0;

        while (matcher.find()) {
            String group = matcher.group();
            String timeUnit = String.valueOf(group.charAt(group.length() - 1));
            int timeValue = Integer.parseInt(group.substring(0, group.length() - 1));
            switch (timeUnit) {
                case "d", "s", "h", "m", "t" -> ticks += (long) UNITS.get(timeUnit) * timeValue;
                default -> {
                    return ArgumentParseResult.failure(new TimeParseException(input, commandContext));
                }
            }
        }
        commandInput.readString();

        return ArgumentParseResult.success(ticks);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        if (input.isEmpty(true)) {
            return IntStream.range(1, 10).boxed()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }

        if (Character.isLetter(input.lastRemainingCharacter())) {
            return Collections.emptyList();
        }

        final String string = input.peekString();
        return Stream.of("d", "h", "m", "s", "t")
                .filter(unit -> !string.contains(unit))
                .map(unit -> string + unit)
                .collect(Collectors.toList());
    }

    public static final class TimeParseException extends ParserException {

        private final String input;

        public TimeParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    TimeParser.class,
                    context,
                    SparrowCaptionKeys.ARGUMENT_PARSE_FAILURE_TIME,
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}
