package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.command.parser.CustomEnchantmentParser;
import net.momirealms.sparrow.bukkit.util.CommandUtils;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class EnchantAdminCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "enchant_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.ENTITY_SELECTOR, MultipleEntitySelectorParser.multipleEntitySelectorParser(false))
                .required("enchantment", CustomEnchantmentParser.enchantmentParser())
                .optional("level", IntegerParser.integerParser(1))
                .optional("slot", EnumParser.enumParser(EquipmentSlot.class))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .flag(manager.flagBuilder("ignore-level"))
                .flag(manager.flagBuilder("ignore-conflict"))
                .flag(manager.flagBuilder("ignore-incompatible"))
                .handler(commandContext -> {
                    Enchantment enchantment = commandContext.get("enchantment");
                    int level = commandContext.getOrDefault("level", 1);
                    Optional<EquipmentSlot> optionalEquipmentSlot = commandContext.optional("slot");
                    EquipmentSlot slot = optionalEquipmentSlot.orElse(EquipmentSlot.HAND);
                    if (!commandContext.flags().hasFlag("ignore-level") && enchantment.getMaxLevel() < level) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_LEVEL);
                        commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(
                                Component.text(level),
                                Component.text(enchantment.getMaxLevel())
                        ));
                        return;
                    }
                    int i = 0;
                    boolean ignoreIncompatible = commandContext.flags().hasFlag("ignore-incompatible");
                    boolean ignoreConflict = commandContext.flags().hasFlag("ignore-conflict");
                    MultipleEntitySelector selector = commandContext.get(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
                    Collection<Entity> targets = selector.values();
                    for (Entity entity : targets) {
                        if (entity instanceof LivingEntity livingEntity) {
                            EntityEquipment entityEquipment = livingEntity.getEquipment();
                            if (entityEquipment != null) {
                                ItemStack itemStack = entityEquipment.getItem(slot);
                                if (!itemStack.isEmpty()) {
                                    if ((!enchantment.canEnchantItem(itemStack) && !ignoreIncompatible) || (hasConflicts(enchantment, itemStack) && !ignoreConflict)) {
                                        if (targets.size() != 1) continue;
                                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_INCOMPATIBLE);
                                        commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(
                                                Component.translatable(itemStack.translationKey())
                                        ));
                                        return;
                                    }
                                    itemStack.addUnsafeEnchantment(enchantment, level);
                                    entityEquipment.setItem(slot, itemStack);
                                    ++i;
                                    continue;
                                }
                                if (targets.size() != 1) continue;
                                commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_ITEMLESS);
                                commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(
                                        Component.text(livingEntity.getName())
                                ));
                                return;
                            }
                        }
                        if (targets.size() != 1) continue;
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_ENTITY);
                        commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(
                                Component.text(entity.getName())
                        ));
                        return;
                    }
                    if (i == 0) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED);
                        return;
                    }

                    CommandUtils.storeSelectorMessage(
                            commandContext,
                            selector,
                            Pair.of(MessageConstants.COMMANDS_ADMIN_ENCHANT_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_ENCHANT_SUCCESS_MULTIPLE),
                            Pair.of(
                                    () -> List.of(getFullName(enchantment, level), Component.text(targets.iterator().next().getName())),
                                    () -> List.of(getFullName(enchantment, level), Component.text(targets.size())
                            )
                    ));
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
        Component component = Component.translatable(enchantment.translationKey());
        if (enchantment.isCursed()) {
            component = component.color(NamedTextColor.RED);
        } else {
            component = component.color(NamedTextColor.GRAY);
        }
        if (level != 1 || enchantment.getMaxLevel() != 1) {
            component = component.append(Component.space()).append(Component.translatable("enchantment.level." + level));
        }
        return component;
    }
}
