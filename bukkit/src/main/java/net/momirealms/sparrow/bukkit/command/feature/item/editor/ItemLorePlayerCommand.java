package net.momirealms.sparrow.bukkit.command.feature.item.editor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.feature.item.SparrowItem;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemLorePlayerCommand extends BukkitCommandFeature<CommandSender> {

    private static final String LORE_META_KEY = "sparrow_lore";

    public ItemLorePlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "itemlore_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .flag(SparrowFlagKeys.LEGACY_COLOR_FLAG)
                .flag(manager.flagBuilder("lore").withComponent(StringParser.greedyFlagYieldingStringParser()).build())
                .flag(manager.flagBuilder("operation").withComponent(EnumParser.enumComponent(Operation.class).parser()).build())
                .flag(manager.flagBuilder("line").withComponent(IntegerParser.integerParser(1)).build())
                .flag(manager.flagBuilder("internal").withComponent(IntegerParser.integerParser(1)).build())
                .flag(manager.flagBuilder("json").build())
                .handler(commandContext -> {
                    boolean hasOps = commandContext.flags().hasFlag("operation");
                    boolean legacy = commandContext.flags().hasFlag(SparrowFlagKeys.LEGACY_COLOR_FLAG);
                    boolean useJson = commandContext.flags().hasFlag("json");
                    ItemStack itemInHand = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_FAILURE_ITEMLESS);
                        return;
                    }
                    SparrowItem<ItemStack> sparrowItem = SparrowBukkitPlugin.getInstance().getItemFactory().wrap(itemInHand.clone());
                    List<String> originalLore = new ArrayList<>(sparrowItem.lore().orElse(List.of()));
                    if (hasOps) {
                        boolean hasLine = commandContext.flags().hasFlag("line");
                        boolean hasLore = commandContext.flags().hasFlag("lore");
                        if (!hasLine) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_FLAG, Component.text("--line"));
                            return;
                        }
                        int line = (int) commandContext.flags().getValue("line").get();
                        Operation operation = (Operation) commandContext.flags().getValue("operation").get();
                        switch (operation) {
                            case INSERT -> {
                                if (line > originalLore.size() + 1) {
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_BOUND,
                                            Component.text(line),
                                            Component.text(1),
                                            Component.text(originalLore.size() + 1)
                                    );
                                    return;
                                }

                                if (!hasLore) {
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_FLAG, Component.text("--lore"));
                                    return;
                                }

                                String lore = (String) commandContext.flags().getValue("lore").get();
                                String json = useJson ? lore : AdventureHelper.componentToJson(AdventureHelper.getMiniMessage().deserialize(
                                        legacy ? AdventureHelper.legacyToMiniMessage("<!i><white>" + lore) : "<!i><white>" + lore
                                ));

                                originalLore.add(line - 1, json);

                                ItemStack modified = sparrowItem.lore(originalLore).load();
                                itemInHand.setItemMeta(modified.getItemMeta());
                                handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_SUCCESS, getQueryResult(commandContext.sender(), originalLore));
                            }
                            case EDIT -> {
                                if (line > originalLore.size()) {
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_BOUND,
                                            Component.text(line),
                                            Component.text(Math.min(originalLore.size(), 1)),
                                            Component.text(originalLore.size())
                                    );
                                    return;
                                }

                                if (!hasLore) {
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_FLAG, Component.text("--lore"));
                                    return;
                                }

                                String lore = (String) commandContext.flags().getValue("lore").get();
                                String json = useJson ? lore : AdventureHelper.componentToJson(AdventureHelper.getMiniMessage().deserialize(
                                        legacy ? AdventureHelper.legacyToMiniMessage("<!i><white>" + lore) : "<!i><white>" + lore
                                ));

                                originalLore.set(line - 1, json);

                                ItemStack modified = sparrowItem.lore(originalLore).load();
                                itemInHand.setItemMeta(modified.getItemMeta());
                                handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_SUCCESS, getQueryResult(commandContext.sender(), originalLore));
                            }
                            case REMOVE -> {
                                if (commandContext.flags().hasFlag("internal")) {
                                    Player player = commandContext.sender();
                                    int internal = (int) commandContext.flags().getValue("internal").get();
                                    Optional<MetadataValue> optionalMetadataValue = player.getMetadata(LORE_META_KEY).stream().findAny();
                                    if (optionalMetadataValue.isEmpty() || optionalMetadataValue.get().asInt() != internal) {
                                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_INVALID);
                                        return;
                                    }
                                }

                                if (line > originalLore.size()) {
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_BOUND,
                                            Component.text(line),
                                            Component.text(Math.min(originalLore.size(), 1)),
                                            Component.text(originalLore.size())
                                    );
                                    return;
                                }

                                originalLore.remove(line - 1);
                                ItemStack modified = sparrowItem.lore(originalLore).load();
                                itemInHand.setItemMeta(modified.getItemMeta());
                                handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_SUCCESS, getQueryResult(commandContext.sender(), originalLore));
                            }
                            case DOWN -> {
                                if (commandContext.flags().hasFlag("internal")) {
                                    Player player = commandContext.sender();
                                    int internal = (int) commandContext.flags().getValue("internal").get();
                                    Optional<MetadataValue> optionalMetadataValue = player.getMetadata(LORE_META_KEY).stream().findAny();
                                    if (optionalMetadataValue.isEmpty() || optionalMetadataValue.get().asInt() != internal) {
                                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_INVALID);
                                        return;
                                    }
                                }
                                if (line >= originalLore.size()) {
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_BOUND,
                                            Component.text(line),
                                            Component.text(Math.min(originalLore.size() - 1, 1)),
                                            Component.text(originalLore.size() - 1)
                                    );
                                    return;
                                }
                                String up = originalLore.get(line - 1);
                                String down = originalLore.get(line - 1 + 1);
                                originalLore.set(line - 1, down);
                                originalLore.set(line - 1 + 1, up);

                                ItemStack modified = sparrowItem.lore(originalLore).load();
                                itemInHand.setItemMeta(modified.getItemMeta());
                                handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_SUCCESS, getQueryResult(commandContext.sender(), originalLore));
                            }
                            case UP -> {
                                if (commandContext.flags().hasFlag("internal")) {
                                    Player player = commandContext.sender();
                                    int internal = (int) commandContext.flags().getValue("internal").get();
                                    Optional<MetadataValue> optionalMetadataValue = player.getMetadata(LORE_META_KEY).stream().findAny();
                                    if (optionalMetadataValue.isEmpty() || optionalMetadataValue.get().asInt() != internal) {
                                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_INVALID);
                                        return;
                                    }
                                }
                                if (line == 1 || line > originalLore.size()) {
                                    if (originalLore.size() == 1 || originalLore.isEmpty()) {
                                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_BOUND,
                                                Component.text(line),
                                                Component.text(0),
                                                Component.text(0)
                                        );
                                    } else {
                                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_FAILURE_BOUND,
                                                Component.text(line),
                                                Component.text(2),
                                                Component.text(originalLore.size())
                                        );
                                    }
                                    return;
                                }

                                String down = originalLore.get(line - 1);
                                String up = originalLore.get(line - 1 - 1);
                                originalLore.set(line - 1, up);
                                originalLore.set(line - 1 - 1, down);

                                ItemStack modified = sparrowItem.lore(originalLore).load();
                                itemInHand.setItemMeta(modified.getItemMeta());
                                handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_LORE_SUCCESS, getQueryResult(commandContext.sender(), originalLore));
                            }
                        }
                    } else {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_QUERY_LORE_SUCCESS, getQueryResult(commandContext.sender(), originalLore));
                    }
                });
    }

    private Component getQueryResult(Player player, List<String> lore) {
        int internalID = getNextInternalID(player);
        TextComponent.Builder component = Component.text();
        for (int i = 0, size = lore.size(); i < size; i++) {
            Component loreComponent = AdventureHelper.getGson().deserialize(lore.get(i));
            String miniMessage = AdventureHelper.getMiniMessage().serialize(loreComponent);
            component.append(Component.text("[" + (i+1) + "] ").color(NamedTextColor.YELLOW));
            component.append(Component.space());
            component.append(Component.text("\"" + lore.get(i) +  "\"")
                    .color(NamedTextColor.GRAY)
                    .hoverEvent(HoverEvent.showText(Component.text("Edit").color(NamedTextColor.GRAY)))
                    .clickEvent(ClickEvent.suggestCommand(getCommandConfig().getUsages().get(0) + " --json --operation edit --line " + (i+1) + " --lore " + lore.get(i)))
            );
            component.append(Component.text("[" + miniMessage +  "]")
                    .color(NamedTextColor.WHITE)
                    .hoverEvent(HoverEvent.showText(Component.text("Edit").color(NamedTextColor.WHITE)))
                    .clickEvent(ClickEvent.suggestCommand(getCommandConfig().getUsages().get(0) + " --operation edit --line " + (i+1) + " --lore " + miniMessage))
            );
            component.append(Component.space());
            component.append(Component.text("[X]")
                    .color(TextColor.fromHexString("#DC143C"))
                    .hoverEvent(HoverEvent.showText(Component.text("Delete").color(TextColor.fromHexString("#DC143C"))))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, getCommandConfig().getUsages().get(0) + " --operation remove --line " + (i+1) + " --internal " + internalID))
            );
            if (i != 0) {
                component.append(Component.space());
                component.append(Component.text("[↑]")
                        .color(TextColor.fromHexString("#7B68EE"))
                        .hoverEvent(HoverEvent.showText(Component.text("Move up").color(TextColor.fromHexString("#7B68EE"))))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, getCommandConfig().getUsages().get(0) + " --operation up --line " + (i+1) + " --internal " + internalID))
                );
            }
            if (i != size - 1) {
                component.append(Component.space());
                component.append(Component.text("[↓]")
                        .color(TextColor.fromHexString("#DA70D6"))
                        .hoverEvent(HoverEvent.showText(Component.text("Move down").color(TextColor.fromHexString("#DA70D6"))))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, getCommandConfig().getUsages().get(0) + " --operation down --line " + (i+1) + " --internal " + internalID))
                );
            }
            component.append(Component.newline());
        }
        component.append(Component.text("[" + (lore.size() + 1) + "] ").color(NamedTextColor.YELLOW));
        component.append(Component.space());
        component.append(Component.text("[+]")
                .color(NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("Insert").color(NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.suggestCommand(getCommandConfig().getUsages().get(0) + " --operation insert --line " + (lore.size() + 1) + " --lore "))
        );
        return component.build();
    }

    private int getNextInternalID(Player player) {
        if (player.hasMetadata(LORE_META_KEY)) {
            Optional<MetadataValue> optionalMetadataValue = player.getMetadata(LORE_META_KEY).stream().findAny();
            if (optionalMetadataValue.isPresent()) {
                int previous = optionalMetadataValue.get().asInt();
                player.setMetadata(LORE_META_KEY, new FixedMetadataValue(SparrowBukkitPlugin.getInstance().getLoader(), previous+1));
                return previous+1;
            } else {
                player.setMetadata(LORE_META_KEY, new FixedMetadataValue(SparrowBukkitPlugin.getInstance().getLoader(), 1));
                return 1;
            }
        } else {
            player.setMetadata(LORE_META_KEY, new FixedMetadataValue(SparrowBukkitPlugin.getInstance().getLoader(), 1));
            return 1;
        }
    }

    public enum Operation {
        INSERT,
        REMOVE,
        EDIT,
        UP,
        DOWN
    }
}
