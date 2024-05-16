package net.momirealms.sparrow.bukkit.command.feature.item;

import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.*;

public class ItemDataPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public ItemDataPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "item_data_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    ItemStack itemInHand = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemInHand.isEmpty()) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_FAILURE_ITEMLESS);
                        return;
                    }
                    Map<String, Object> readableMap = ItemStackUtils.toReadableMap(itemInHand);
                    readableMap.remove("rtagDataVersion");
                    List<String> readableList = mapToList(readableMap);
                    StringJoiner joiner = new StringJoiner("<newline><reset>");
                    for (String text : readableList) {
                        joiner.add(text);
                    }
                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_DATA, AdventureHelper.getMiniMessage().deserialize(joiner.toString()));
                });
    }

    private List<String> mapToList(Map<String, Object> readableDataMap) {
        List<String> list = new ArrayList<>();
        mapToList(readableDataMap, list, 0, false);
        return list;
    }

    @SuppressWarnings("unchecked")
    private void mapToList(Map<String, Object> map, List<String> readableList, int loopTimes, boolean isMapList) {
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object nbt = entry.getValue();
            if (nbt instanceof List<?> list) {
                if (isMapList && first) {
                    first = false;
                    readableList.add("  ".repeat(loopTimes - 1) +
                            "<#F5F5F5>- <gradient:#FFD700:#FFFACD><hover:show_text:'<yellow>List'>" + entry.getKey() + "</hover></gradient>:</#F5F5F5>");
                } else {
                    readableList.add("  ".repeat(loopTimes) +
                            "<#F5F5F5><gradient:#FFD700:#FFFACD><hover:show_text:'<yellow>List'>" + entry.getKey() + "</hover></gradient>:</#F5F5F5>");
                }
                for (Object value : list) {
                    if (value instanceof Map<?,?> innerDataMap) {
                        mapToList((Map<String, Object>) innerDataMap, readableList, loopTimes + 2, true);
                    } else {
                        readableList.add("  ".repeat(loopTimes + 1)
                                + "<#F5F5F5>- <hover:show_text:'<yellow>Copy'><click:suggest_command:'" + value + "'>" + value + "</click></hover></#F5F5F5>");
                    }
                }
            } else if (nbt instanceof Map<?,?> innerMap) {
                if (isMapList && first) {
                    first = false;
                    readableList.add("  ".repeat(loopTimes - 1) +
                            "<#F5F5F5>- <gradient:#FFD700:#FFFACD><hover:show_text:'<yellow>Map'>" + entry.getKey() + "</hover></gradient>:</#F5F5F5>");
                } else {
                    readableList.add("  ".repeat(loopTimes) +
                            "<#F5F5F5><gradient:#FFD700:#FFFACD><hover:show_text:'<yellow>Map'>" + entry.getKey() + "</hover></gradient>:");
                }
                mapToList((Map<String, Object>) innerMap, readableList, loopTimes + 1, false);
            } else  {
                String value;
                if (nbt.getClass().isArray()) {
                    if (nbt instanceof Object[]) {
                        value = Arrays.deepToString((Object[]) nbt);
                    } else if (nbt instanceof int[]) {
                        value = Arrays.toString((int[]) nbt);
                    } else if (nbt instanceof long[]) {
                        value = Arrays.toString((long[]) nbt);
                    } else if (nbt instanceof double[]) {
                        value = Arrays.toString((double[]) nbt);
                    } else if (nbt instanceof float[]) {
                        value = Arrays.toString((float[]) nbt);
                    } else if (nbt instanceof boolean[]) {
                        value = Arrays.toString((boolean[]) nbt);
                    } else if (nbt instanceof byte[]) {
                        value = Arrays.toString((byte[]) nbt);
                    } else if (nbt instanceof char[]) {
                        value = Arrays.toString((char[]) nbt);
                    } else if (nbt instanceof short[]) {
                        value = Arrays.toString((short[]) nbt);
                    } else {
                        value = "Unknown array type";
                    }
                } else {
                    value = nbt.toString();
                }

                if (isMapList && first) {
                    first = false;
                    readableList.add("  ".repeat(loopTimes - 1) +
                            "<#F5F5F5>- <gradient:#FFD700:#FFFACD><hover:show_text:'<yellow>" + nbt.getClass().getSimpleName() + "'>" + entry.getKey() + "</hover></gradient>" +
                            ": " +
                            "<#F5F5F5><hover:show_text:'<yellow>Copy'><click:suggest_command:'" + value + "'>" + value + "</click></hover></#F5F5F5>");
                } else {
                    readableList.add("  ".repeat(loopTimes) +
                            "<#F5F5F5><gradient:#FFD700:#FFFACD><hover:show_text:'<yellow>" + nbt.getClass().getSimpleName() + "'>" + entry.getKey() + "</hover></gradient>" +
                            ": " +
                            "<hover:show_text:'<yellow>Copy'><click:suggest_command:'" + value + "'>" + value + "</click></hover></#F5F5F5>");
                }
            }
        }
    }
}
