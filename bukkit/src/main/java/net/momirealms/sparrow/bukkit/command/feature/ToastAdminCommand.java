package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.data.ProtoItemStack;
import org.incendo.cloud.bukkit.parser.ItemStackParser;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.StringParser;

public class ToastAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "toast_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("type", EnumParser.enumParser(AdvancementType.class))
                .required("item", ItemStackParser.itemStackParser())
                .required("message", StringParser.greedyFlagYieldingStringParser())
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    ProtoItemStack itemStack = commandContext.get("item");
                    ItemStack bukkitStack = itemStack.createItemStack(1, true);
                    String message = commandContext.get("message");
                    String json = GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(message));
                    AdvancementType type = commandContext.get("type");
                    for (Player player : selector.values()) {
                        SparrowBukkitPlugin.getInstance().getCoreNMSBridge().getHeart().sendToast(
                                player,
                                bukkitStack,
                                json,
                                type.name()
                        );
                    }
                });
    }

    public enum AdvancementType {
        TASK,
        GOAL,
        CHALLENGE
    }
}
