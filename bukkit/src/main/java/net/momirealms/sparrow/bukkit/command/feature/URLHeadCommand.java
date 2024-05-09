package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.parser.URLParser;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;

import java.net.URL;

public class URLHeadCommand extends AbstractCommandFeature<CommandSender> {
    @Override
    public String getFeatureID() {
        return "urlhead_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .required("url", URLParser.urlParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    URL url = commandContext.get("url");
                    boolean silent = commandContext.flags().hasFlag("silent");
                    var players = selector.values();

                })
    }
}
