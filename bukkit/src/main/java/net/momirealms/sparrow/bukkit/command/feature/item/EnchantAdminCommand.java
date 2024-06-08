package net.momirealms.sparrow.bukkit.command.feature.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.command.parser.CustomEnchantmentParser;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
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
import org.incendo.cloud.bukkit.parser.EnchantmentParser;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.Collection;

public class EnchantAdminCommand extends BukkitCommandFeature<CommandSender> {

    public EnchantAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "enchant_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.ENTITY_SELECTOR, MultipleEntitySelectorParser.multipleEntitySelectorParser(false))
                .meta(SparrowMetaKeys.ALLOW_EMPTY_ENTITY_SELECTOR, false)
                .required("enchantment", CustomEnchantmentParser.enchantmentParser())
                .optional("level", IntegerParser.integerParser(1))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .flag(manager.flagBuilder("ignore-level"))
                .flag(manager.flagBuilder("ignore-conflict"))
                .flag(manager.flagBuilder("ignore-incompatible"))
                .flag(manager.flagBuilder("slot").withComponent(EnumParser.enumParser(EquipmentSlot.class)))
                .handler(commandContext -> {
                    Enchantment enchantment = commandContext.get("enchantment");
                    int level = commandContext.getOrDefault("level", 1);
                    EquipmentSlot slot = commandContext.flags().hasFlag("slot") ? (EquipmentSlot) commandContext.flags().getValue("slot").get() : EquipmentSlot.HAND;
                    if (!commandContext.flags().hasFlag("ignore-level") && enchantment.getMaxLevel() < level) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_LEVEL, Component.text(level), Component.text(enchantment.getMaxLevel()));
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
                                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_INCOMPATIBLE, Component.translatable(itemStack.translationKey()));
                                        return;
                                    }
                                    itemStack.addUnsafeEnchantment(enchantment, level);
                                    entityEquipment.setItem(slot, itemStack);
                                    ++i;
                                    continue;
                                }
                                if (targets.size() != 1) continue;
                                handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_ITEMLESS, Component.text(livingEntity.getName()));
                                return;
                            }
                        }
                        if (targets.size() != 1) continue;
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_ENTITY, Component.text(entity.getName()));
                        return;
                    }
                    if (i == 0) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED);
                        return;
                    }
                    var pair = resolveSelector(selector, MessageConstants.COMMANDS_ADMIN_ENCHANT_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_ENCHANT_SUCCESS_MULTIPLE);
                    handleFeedback(commandContext, pair.left(), getFullName(enchantment, level), pair.right());
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
