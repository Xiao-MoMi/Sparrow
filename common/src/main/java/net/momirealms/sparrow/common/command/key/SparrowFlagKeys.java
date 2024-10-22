package net.momirealms.sparrow.common.command.key;

import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.parser.standard.StringParser;

public final class SparrowFlagKeys {
    public static final String SILENT = "silent";
    public static final CommandFlag<Void> SILENT_FLAG = CommandFlag.builder(SILENT).withAliases("s").build();
    public static final String LEGACY_COLOR = "legacy-color";
    public static final CommandFlag<Void> LEGACY_COLOR_FLAG = CommandFlag.builder(LEGACY_COLOR).withAliases("l").build();
    public static final String PARSE = "parse";
    public static final CommandFlag<Void> PARSE_FLAG = CommandFlag.builder(PARSE).withAliases("p").build();
    public static final String TITLE = "title";
    public static final CommandFlag<String> TITLE_FLAG = CommandFlag.builder(TITLE).withAliases("t").withComponent(StringParser.greedyFlagYieldingStringParser()).build();
}
