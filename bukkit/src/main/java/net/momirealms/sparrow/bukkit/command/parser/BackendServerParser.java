package net.momirealms.sparrow.bukkit.command.parser;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

public class BackendServerParser<C> implements ArgumentParser<C, String>,
        BlockingSuggestionProvider.Strings<C> {

    public static <C> @NonNull ParserDescriptor<C, String> backendServerParser() {
        return ParserDescriptor.of(new BackendServerParser<>(), String.class);
    }

    public static <C> CommandComponent.@NonNull Builder<C, String> backendServerComponent() {
        return CommandComponent.<C, String>builder().parser(backendServerParser());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull String> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        return ArgumentParseResult.success(commandInput.readString());
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return SparrowBukkitPlugin.getInstance().getBungeeManager().getBackendServers();
    }
}
