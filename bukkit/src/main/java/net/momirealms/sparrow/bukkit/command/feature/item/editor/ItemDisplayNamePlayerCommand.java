package net.momirealms.sparrow.bukkit.command.feature.item.editor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Optional;

public class ItemDisplayNamePlayerCommand extends BukkitCommandFeature<CommandSender> {

    public ItemDisplayNamePlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "item_displayname_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .optional("displayname", StringParser.greedyFlagYieldingStringParser())
                .flag(manager.flagBuilder("json").build())
                .flag(SparrowFlagKeys.LEGACY_COLOR_FLAG)
                .handler(commandContext -> {
                    boolean useJson = commandContext.flags().hasFlag("json");
                    Optional<String> optional = commandContext.optional("displayname");
                    ItemStack itemInHand = commandContext.sender().getInventory().getItemInMainHand();
                    if (optional.isPresent()) {
                        boolean legacy = commandContext.flags().hasFlag(SparrowFlagKeys.LEGACY_COLOR_FLAG);
                        Component name = useJson ? AdventureHelper.getGson().deserialize(optional.get()) : AdventureHelper.getMiniMessage().deserialize(
                                legacy ? AdventureHelper.legacyToMiniMessage("<!i>" + optional.get()) : "<!i>" + optional.get()
                        );
                        String json = useJson ? optional.get() : AdventureHelper.componentToJson(name);
                        ItemStack modified = SparrowBukkitPlugin.getInstance().getItemFactory()
                                .wrap(itemInHand.clone())
                                .displayName(json)
                                .load();
                        itemInHand.setItemMeta(modified.getItemMeta());
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_DISPLAY_NAME_SUCCESS, name);
                    } else {
                        Optional<String> name = SparrowBukkitPlugin.getInstance().getItemFactory()
                                .wrap(itemInHand)
                                .displayName();
                        if (name.isPresent()) {
                            Component nameComponent = AdventureHelper.getGson().deserialize(name.get());
                            String miniMessage = AdventureHelper.getMiniMessage().serialize(nameComponent);
                            TextComponent.Builder component = Component.text();
                            component.append(Component.text("\"" + name.get() +  "\"")
                                    .color(NamedTextColor.GRAY)
                                    .hoverEvent(HoverEvent.showText(Component.text("Edit").color(NamedTextColor.GRAY)))
                                    .clickEvent(ClickEvent.suggestCommand(getCommandConfig().getUsages().get(0) + " " + name.get() + " --json"))
                            );
                            component.append(Component.text("[" + miniMessage +  "]")
                                    .color(NamedTextColor.WHITE)
                                    .hoverEvent(HoverEvent.showText(Component.text("Edit").color(NamedTextColor.WHITE)))
                                    .clickEvent(ClickEvent.suggestCommand(getCommandConfig().getUsages().get(0) + " " + miniMessage))
                            );
                            handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_QUERY_DISPLAY_NAME_SUCCESS, component.build());
                        } else {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_QUERY_DISPLAY_NAME_FAILURE);
                        }
                    }
                });
    }
}
