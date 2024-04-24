package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.command.parser.CustomEnchantmentParser;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.Collection;

public class EnchantAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "enchant_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("entity", MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .required("enchantment", CustomEnchantmentParser.enchantmentParser())
                .optional("level", IntegerParser.integerParser(1))
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .flag(manager.flagBuilder("ignore-level"))
                .flag(manager.flagBuilder("ignore-conflict"))
                .flag(manager.flagBuilder("ignore-incompatible"))
                .handler(commandContext -> {
                    Enchantment enchantment = commandContext.get("enchantment");
                    int level = commandContext.getOrDefault("level", 1);
                    if (!commandContext.flags().hasFlag("ignore-level") && enchantment.getMaxLevel() < level) {
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(Component.translatable("commands.enchant.failed.level", Component.text(level), Component.text(enchantment.getMaxLevel())).color(NamedTextColor.RED));
                        return;
                    }
                    int i = 0;
                    boolean silent = commandContext.flags().hasFlag("silent");
                    boolean ignoreIncompatible = commandContext.flags().hasFlag("ignore-incompatible");
                    boolean ignoreConflict = commandContext.flags().hasFlag("ignore-conflict");
                    MultipleEntitySelector selector = commandContext.get("entity");
                    Collection<Entity> targets = selector.values();
                    for (Entity entity : targets) {
                        if (entity instanceof LivingEntity livingEntity) {
                            EntityEquipment entityEquipment = livingEntity.getEquipment();
                            if (entityEquipment != null) {
                                ItemStack itemStack = entityEquipment.getItemInMainHand();
                                if (!itemStack.isEmpty()) {
                                    if ((!enchantment.canEnchantItem(itemStack) && !ignoreIncompatible) || (hasConflicts(enchantment, itemStack) && !ignoreConflict)) {
                                        if (targets.size() != 1) continue;
                                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                                .wrap(commandContext.sender())
                                                .sendMessage(Component.translatable("commands.enchant.failed.incompatible", Component.translatable(itemStack.translationKey())).color(NamedTextColor.RED));
                                        return;
                                    }
                                    itemStack.addUnsafeEnchantment(enchantment, level);
                                    entityEquipment.setItemInMainHand(itemStack);
                                    ++i;
                                    continue;
                                }
                                if (targets.size() != 1) continue;
                                if (!silent)
                                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                                            .wrap(commandContext.sender())
                                            .sendMessage(Component.translatable("commands.enchant.failed.itemless", Component.text(livingEntity.getName())).color(NamedTextColor.RED));
                                return;
                            }
                        }
                        if (targets.size() != 1) continue;
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(Component.translatable("commands.enchant.failed.entity", Component.text(entity.getName())).color(NamedTextColor.RED));
                        return;
                    }
                    if (i == 0) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(Component.translatable("commands.enchant.failed").color(NamedTextColor.RED));
                        return;
                    }
                    if (targets.size() == 1) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(Component.translatable("commands.enchant.success.single", getFullName(enchantment, level), targets.iterator().next().name()));
                    } else {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(Component.translatable("commands.enchant.success.multiple", getFullName(enchantment, level), Component.text(targets.size())));
                    }
                });
    }

    private boolean hasConflicts(Enchantment enchantment, ItemStack itemStack) {
        for (Enchantment applied : itemStack.getEnchantments().keySet()) {
            if (applied.conflictsWith(enchantment)) {
                return true;
            }
        }
        return false;
    }

    private Component getFullName(Enchantment enchantment, int level) {
        Component component = Component.text(enchantment.getKey().asString());
        if (enchantment.isCursed()) {
            component = component.color(NamedTextColor.RED);
        } else {
            component = component.color(NamedTextColor.GRAY);
        }
        if (level != 1 || enchantment.getMaxLevel() != 1) {
            component = component.append(Component.text(" ")).append(Component.translatable("enchantment.level." + level));
        }
        return component;
    }
}
