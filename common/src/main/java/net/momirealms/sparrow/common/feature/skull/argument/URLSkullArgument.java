package net.momirealms.sparrow.common.feature.skull.argument;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public record URLSkullArgument(@NotNull URL url) implements SkullArgument {
}
