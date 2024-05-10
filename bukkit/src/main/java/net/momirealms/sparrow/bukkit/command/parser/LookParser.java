package net.momirealms.sparrow.bukkit.command.parser;

import net.momirealms.sparrow.common.locale.SparrowCaptionKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.BukkitCaptionKeys;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class LookParser<C> implements ArgumentParser<C, Entity>, BlockingSuggestionProvider.Strings<C> {

	public static <C> @NonNull ParserDescriptor<C, Entity> lookParser() {
		return ParserDescriptor.of(new LookParser<>(), Entity.class);
	}

	@Override
	public @NonNull ArgumentParseResult<Entity> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
		String type = commandContext.get("target");

		final String input = commandInput.peekString();
		Entity target;
		if (type.equals("player")) {
			Player player = Bukkit.getPlayer(commandInput.readString());
			if (player == null) return ArgumentParseResult.failure(new LookParseException(input, commandContext, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER));
			target = player;
		} else if (type.equals("entity_uuid")) {
			String uuidString = commandInput.readString();
			UUID uuid;
			try {
				uuid = UUID.fromString(uuidString);
			} catch (IllegalArgumentException e) {
				return ArgumentParseResult.failure(new LookParseException(uuidString, commandContext, SparrowCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID));
			}
			Entity entity = Bukkit.getEntity(uuid);
			if (entity == null) return ArgumentParseResult.failure(new LookParseException(input, commandContext, SparrowCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID));
			target = entity;
		} else {
			return ArgumentParseResult.failure(new Throwable());
		}
		return ArgumentParseResult.success(target);
	}

	@Override
	public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
		String type = commandContext.get("target");

		if (type.equals("player")) {
			final List<String> players = new ArrayList<>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				players.add(p.getName());
			}
			return players;
		}

		return Collections.emptyList();
	}

	public static final class LookParseException extends ParserException {

		private final String input;

		public LookParseException(
				final @NonNull String input,
				final @NonNull CommandContext<?> context,
				Caption caption
		) {
			super(
					LookParser.class,
					context,
					caption,
					CaptionVariable.of("input", input));
			this.input = input;
		}


		public @NonNull String input() {
			return this.input;
		}
	}
}
