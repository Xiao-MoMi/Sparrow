package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Optional;

public class TitleAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "title_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("fadeIn", IntegerParser.integerParser(0))
                .required("stay", IntegerParser.integerParser(0))
                .required("fadeOut", IntegerParser.integerParser(0))
                .required("title", StringParser.greedyFlagYieldingStringParser(), Description.of("Use \\n to split title and subtitle"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    int fadeIn = commandContext.get("fadeIn");
                    int stay = commandContext.get("stay");
                    int fadeOut = commandContext.get("fadeIn");
                    String titleContent = commandContext.get("title");
                    String title;
                    String subTitle;
                    String[] split = titleContent.split("\\\\n");
                    if (split.length > 2) {
                        return;
                    }
                    title = split[0].equals("") ? null : split[0];
                    subTitle = split.length == 2 && !split[1].equals("") ? split[1] : null;
                    for (Player player : selector.values()) {
                        SparrowBukkitPlugin.getInstance().getCoreNMSBridge().getHeart().sendTitle(
                                player,
                                Optional.ofNullable(title).map(t -> GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(t))).orElse(null),
                                Optional.ofNullable(subTitle).map(t -> GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(t))).orElse(null),
                                fadeIn,
                                stay,
                                fadeOut
                        );
                    }
                });
    }
}
