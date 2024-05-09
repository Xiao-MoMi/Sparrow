package net.momirealms.sparrow.common.feature.skull.argument;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UUIDSkullArgument(@NotNull UUID uniqueId) implements SkullArgument {
}
