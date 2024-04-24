package net.momirealms.sparrow.bukkit.command.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.BukkitCaptionKeys;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.ArrayList;
import java.util.List;

public final class CustomEnchantmentParser<C> implements ArgumentParser<C, Enchantment>,
        BlockingSuggestionProvider.Strings<C> {

    public static <C> @NonNull ParserDescriptor<C, Enchantment> enchantmentParser() {
        return ParserDescriptor.of(new CustomEnchantmentParser<>(), Enchantment.class);
    }

    public static <C> CommandComponent.@NonNull Builder<C, Enchantment> enchantmentComponent() {
        return CommandComponent.<C, Enchantment>builder().parser(enchantmentParser());
    }

    @Override
    public @NonNull ArgumentParseResult<Enchantment> parse(
            final @NonNull CommandContext<C> commandContext,
            final @NonNull CommandInput commandInput
    ) {
        final String input = commandInput.peekString();

        final NamespacedKey key;
        try {
            if (input.contains(":")) {
                key = new NamespacedKey(commandInput.readUntilAndSkip(':'), commandInput.readString());
            } else {
                key = NamespacedKey.minecraft(commandInput.readString());
            }
        } catch (final Exception ex) {
            return ArgumentParseResult.failure(new EnchantmentParseException(input, commandContext));
        }

        final Enchantment enchantment = Registry.ENCHANTMENT.get(key);
        if (enchantment == null) {
            return ArgumentParseResult.failure(new EnchantmentParseException(input, commandContext));
        }
        return ArgumentParseResult.success(enchantment);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(final @NonNull CommandContext<C> commandContext,
                                                                final @NonNull CommandInput input) {
        final List<String> completions = new ArrayList<>();
        for (Enchantment value : Registry.ENCHANTMENT) {
            completions.add(value.getKey().asString());
        }
        return completions;
    }

    public static final class EnchantmentParseException extends ParserException {

        private final String input;

        public EnchantmentParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    CustomEnchantmentParser.class,
                    context,
                    BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_ENCHANTMENT,
                    CaptionVariable.of("input", input)
            );
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }
    }
}
