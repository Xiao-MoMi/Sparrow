package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.minecraft.extras.parser.TextColorParser;
import org.incendo.cloud.parser.standard.EnumParser;

import java.util.Optional;

public class DyeAdminCommand extends BukkitCommandFeature<CommandSender> {

    public DyeAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "dye_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.ENTITY_SELECTOR, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_ENTITY_SELECTOR, false)
                .required(SparrowBukkitArgumentKeys.TEXT_COLOR, TextColorParser.textColorParser())
                .optional("slot", EnumParser.enumParser(EquipmentSlot.class))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    TextColor textColor = commandContext.get(SparrowBukkitArgumentKeys.TEXT_COLOR);
                    Optional<EquipmentSlot> optionalEquipmentSlot = commandContext.optional("slot");
                    EquipmentSlot slot = optionalEquipmentSlot.orElse(EquipmentSlot.HAND);
                    MultipleEntitySelector selector = commandContext.get(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
                    var entities = selector.values();
                    int i = 0;
                    Color color = Color.fromRGB(textColor.red(), textColor.green(), textColor.blue());
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity livingEntity) {

                            EntityEquipment entityEquipment = livingEntity.getEquipment();
                            if (entityEquipment == null) {
                                continue;
                            }
                            ItemStack itemStack = entityEquipment.getItem(slot);
                            if (!itemStack.isEmpty()) {
                                if (!(itemStack.getItemMeta() instanceof ColorableArmorMeta meta)) {
                                    if (entities.size() != 1) continue;
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_DYE_FAILED_INCOMPATIBLE, Component.translatable(itemStack.translationKey()));
                                    return;
                                }
                                meta.setColor(color);
                                itemStack.setItemMeta(meta);
                                entityEquipment.setItem(slot, itemStack);
                                ++i;
                                continue;
                            }

                            if (entities.size() != 1) continue;
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_DYE_FAILED_ITEMLESS, Component.text(livingEntity.getName()));
                            return;
                        }
                        if (entities.size() != 1) continue;
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_DYE_FAILED_ENTITY, Component.text(entity.getName()));
                        return;
                    }

                    if (i == 0) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_DYE_FAILED);
                        return;
                    }

                    var pair = resolveSelector(selector, MessageConstants.COMMANDS_ADMIN_DYE_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_DYE_SUCCESS_MULTIPLE);
                    handleFeedback(commandContext, pair.left(), pair.right());
                });
    }
}
