package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.parser.PlayerParser;

@SuppressWarnings("DuplicatedCode")
public class HatAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "hat_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(Command.Builder<CommandSender> builder) {
        return builder
                .required("player", PlayerParser.playerParser())
                .handler(commandContext -> {
                    final Player player = commandContext.get("player");
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand.getType() == Material.AIR) return;
                    ItemStack previous = player.getInventory().getHelmet();
                    if (previous != null && previous.getEnchantmentLevel(Enchantment.BINDING_CURSE) != 0) {
                        return;
                    }
                    player.getInventory().setHelmet(itemInHand);
                    if (previous != null && previous.getType() != Material.AIR) {
                        player.getInventory().setItemInMainHand(previous);
                    } else {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                });
    }
}
