package net.momirealms.sparrow.common.command.key;

import org.incendo.cloud.parser.flag.CommandFlag;

public final class SparrowFlagKeys {
    public static final String SILENT = "silent";
    public static final CommandFlag<Void> SILENT_FLAG = CommandFlag.builder(SILENT).withAliases("s").build();
    public static final String LEGACY_COLOR = "legacy-color";
    public static final CommandFlag<Void> LEGACY_COLOR_FLAG = CommandFlag.builder(LEGACY_COLOR).withAliases("l").build();
}
