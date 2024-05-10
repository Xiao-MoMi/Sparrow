package net.momirealms.sparrow.common.plugin.version;

import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.dvs.versioning.AutomaticVersioning;
import org.jetbrains.annotations.NotNull;

public class SparrowVersioning extends AutomaticVersioning {

    public static final Pattern PATTERN = new Pattern(
            Segment.range(0, Integer.MAX_VALUE),
            Segment.literal("."),
            Segment.range(0, Integer.MAX_VALUE),
            Segment.literal("."),
            Segment.range(0, Integer.MAX_VALUE)
    );

    public SparrowVersioning(@NotNull String route) {
        super(PATTERN, route);
    }
}
