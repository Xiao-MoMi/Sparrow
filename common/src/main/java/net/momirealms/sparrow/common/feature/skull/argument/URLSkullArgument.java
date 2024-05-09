package net.momirealms.sparrow.common.feature.skull.argument;

import org.jetbrains.annotations.Nullable;

import java.net.URL;

public record URLSkullArgument(@Nullable URL url) implements SkullArgument {
}
