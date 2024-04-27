package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.Message;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
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
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .required("fadeIn", IntegerParser.integerParser(0))
                .required("stay", IntegerParser.integerParser(0))
                .required("fadeOut", IntegerParser.integerParser(0))
                .required("title", StringParser.greedyFlagYieldingStringParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .flag(manager.flagBuilder("legacy-color").withAliases("l"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    var players = selector.values();
                    boolean legacy = commandContext.flags().hasFlag("legacy-color");
                    int fadeIn = commandContext.get("fadeIn");
                    int stay = commandContext.get("stay");
                    int fadeOut = commandContext.get("fadeIn");
                    boolean silent = commandContext.flags().hasFlag("silent");
                    String titleContent = commandContext.get("title");
                    String title;
                    String subTitle;
                    String[] split = titleContent.split("\\\\n");
                    if (split.length > 2) {
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_TITLE_FAILED_FORMAT.build()
                                            ),
                                            true
                                    );
                        }
                        return;
                    }
                    title = split[0].isEmpty() ? null : split[0];
                    subTitle = split.length == 2 && !split[1].isEmpty() ? split[1] : null;
                    for (Player player : players) {
                        SparrowBukkitPlugin.getInstance().getCoreNMSBridge().getHeart().sendTitle(
                                player,
                                Optional.ofNullable(title)
                                        .map(t -> AdventureHelper.componentToJson(
                                                AdventureHelper.getMiniMessage().deserialize(legacy ? AdventureHelper.legacyToMiniMessage(t) : t))
                                        )
                                        .orElse(null),
                                Optional.ofNullable(subTitle)
                                        .map(t -> AdventureHelper.componentToJson(
                                                AdventureHelper.getMiniMessage().deserialize(legacy ? AdventureHelper.legacyToMiniMessage(t) : t))
                                        )
                                        .orElse(null),
                                fadeIn,
                                stay,
                                fadeOut
                        );
                    }
                    if (!silent) {
                        if (players.size() == 1) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_TITLE_SUCCESS_SINGLE
                                                            .arguments(Component.text(players.iterator().next().getName()))
                                                            .build()
                                            ),
                                            true
                                    );
                        } else {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_TITLE_SUCCESS_MULTIPLE
                                                            .arguments(Component.text(players.size()))
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                    }
                });
    }
}
