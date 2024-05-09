package net.momirealms.sparrow.bukkit.command.parser;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class HeavyOfflinePlayerParser<C> implements ArgumentParser<C, OfflinePlayer>,
        BlockingSuggestionProvider.Strings<C> {

    public static <C> @NonNull ParserDescriptor<C, OfflinePlayer> heavyOfflinePlayerParser() {
        return ParserDescriptor.of(new HeavyOfflinePlayerParser<>(), OfflinePlayer.class);
    }

    public static <C> CommandComponent.@NonNull Builder<C, OfflinePlayer> heavyOfflinePlayerComponent() {
        return CommandComponent.<C, OfflinePlayer>builder().parser(heavyOfflinePlayerParser());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull OfflinePlayer> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        final String input = commandInput.peekString();
        if (input.length() > 16) {
            return ArgumentParseResult.failure(new OfflinePlayerParser.OfflinePlayerParseException(input, commandContext));
        }

        final OfflinePlayer player;
        try {
            player = Bukkit.getOfflinePlayer(input);
        } catch (final Exception e) {
            return ArgumentParseResult.failure(new OfflinePlayerParser.OfflinePlayerParseException(input, commandContext));
        }
        commandInput.readString();

        return ArgumentParseResult.success(player);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(player -> !player.isOnline())
                .map(offlinePlayer -> Optional.ofNullable(offlinePlayer.getName()).orElse(String.valueOf(offlinePlayer.getUniqueId())))
                .collect(Collectors.toList());
    }
}
