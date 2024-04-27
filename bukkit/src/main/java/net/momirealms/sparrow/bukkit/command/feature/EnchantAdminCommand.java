package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.parser.CustomEnchantmentParser;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
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
import java.util.Optional;

public class EnchantAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "enchant_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("entity", MultipleEntitySelectorParser.multipleEntitySelectorParser(false))
                .required("enchantment", CustomEnchantmentParser.enchantmentParser())
                .optional("level", IntegerParser.integerParser(1))
                .optional("slot", EnumParser.enumParser(EquipmentSlot.class))
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .flag(manager.flagBuilder("ignore-level"))
                .flag(manager.flagBuilder("ignore-conflict"))
                .flag(manager.flagBuilder("ignore-incompatible"))
                .handler(commandContext -> {
                    Enchantment enchantment = commandContext.get("enchantment");
                    boolean silent = commandContext.flags().hasFlag("silent");
                    int level = commandContext.getOrDefault("level", 1);
                    Optional<EquipmentSlot> optionalEquipmentSlot = commandContext.optional("slot");
                    EquipmentSlot slot = optionalEquipmentSlot.orElse(EquipmentSlot.HAND);
                    if (!commandContext.flags().hasFlag("ignore-level") && enchantment.getMaxLevel() < level) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_LEVEL
                                                            .arguments(Component.text(level), Component.text(enchantment.getMaxLevel()))
                                                            .build()
                                            )
                                    );
                        return;
                    }
                    int i = 0;
                    boolean ignoreIncompatible = commandContext.flags().hasFlag("ignore-incompatible");
                    boolean ignoreConflict = commandContext.flags().hasFlag("ignore-conflict");
                    MultipleEntitySelector selector = commandContext.get("entity");
                    Collection<Entity> targets = selector.values();
                    for (Entity entity : targets) {
                        if (entity instanceof LivingEntity livingEntity) {
                            EntityEquipment entityEquipment = livingEntity.getEquipment();
                            if (entityEquipment != null) {
                                ItemStack itemStack = entityEquipment.getItem(slot);
                                if (!itemStack.isEmpty()) {
                                    if ((!enchantment.canEnchantItem(itemStack) && !ignoreIncompatible) || (hasConflicts(enchantment, itemStack) && !ignoreConflict)) {
                                        if (targets.size() != 1) continue;
                                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                                .wrap(commandContext.sender())
                                                .sendMessage(
                                                        TranslationManager.render(
                                                                MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_INCOMPATIBLE
                                                                        .arguments(Component.translatable(itemStack.translationKey()))
                                                                        .build()
                                                        )
                                                );
                                        return;
                                    }
                                    itemStack.addUnsafeEnchantment(enchantment, level);
                                    entityEquipment.setItem(slot, itemStack);
                                    ++i;
                                    continue;
                                }
                                if (targets.size() != 1) continue;
                                if (!silent)
                                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                                            .wrap(commandContext.sender())
                                            .sendMessage(
                                                    TranslationManager.render(
                                                            MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_ITEMLESS
                                                                    .arguments(Component.text(livingEntity.getName()))
                                                                    .build()
                                                    )
                                            );
                                return;
                            }
                        }
                        if (targets.size() != 1) continue;
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED_ENTITY
                                                            .arguments(Component.text(entity.getName()))
                                                            .build()
                                            )
                                    );
                        return;
                    }
                    if (i == 0) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_ENCHANT_FAILED
                                                            .build()
                                            )
                                    );
                        return;
                    }
                    if (targets.size() == 1) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_ENCHANT_SUCCESS_SINGLE
                                                            .arguments(getFullName(enchantment, level), Component.text(targets.iterator().next().getName()))
                                                            .build()
                                            )
                                    );
                    } else {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_ENCHANT_SUCCESS_MULTIPLE
                                                            .arguments(getFullName(enchantment, level), Component.text(targets.size()))
                                                            .build()
                                            )
                                    );
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
