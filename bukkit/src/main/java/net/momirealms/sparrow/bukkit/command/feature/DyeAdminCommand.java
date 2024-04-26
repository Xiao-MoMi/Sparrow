package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.locale.Message;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.minecraft.extras.parser.TextColorParser;

public class DyeAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "dye_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("entity", MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .required("color", TextColorParser.textColorParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    TextColor textColor = commandContext.get("color");
                    boolean silent = commandContext.flags().hasFlag("silent");
                    MultipleEntitySelector selector = commandContext.get("entity");
                    var entities = selector.values();
                    if (entities.size() == 0) {
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.ARGUMENT_ENTITY_NOTFOUND_ENTITY.build()
                                            ),
                                            true
                                    );
                        }
                        return;
                    }
                    int i = 0;
                    Color color = Color.fromRGB(textColor.red(), textColor.green(), textColor.blue());
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity livingEntity) {
                            EntityEquipment entityEquipment = livingEntity.getEquipment();
                            if (entityEquipment != null) {
                                ItemStack itemStack = entityEquipment.getItemInMainHand();
                                if (!itemStack.isEmpty()) {
                                    if (!(itemStack.getItemMeta() instanceof ColorableArmorMeta meta)) {
                                        if (entities.size() != 1) continue;
                                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                                .wrap(commandContext.sender())
                                                .sendMessage(
                                                        TranslationManager.render(
                                                                Message.COMMANDS_ADMIN_DYE_FAILED_INCOMPATIBLE
                                                                        .arguments(Component.translatable(itemStack.translationKey()))
                                                                        .build()
                                                        )
                                                );
                                        return;
                                    }
                                    meta.setColor(color);
                                    itemStack.setItemMeta(meta);
                                    entityEquipment.setItemInMainHand(itemStack);
                                    ++i;
                                    continue;
                                }
                                if (entities.size() != 1) continue;
                                if (!silent)
                                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                                            .wrap(commandContext.sender())
                                            .sendMessage(
                                                    TranslationManager.render(
                                                            Message.COMMANDS_ADMIN_DYE_FAILED_ITEMLESS
                                                                    .arguments(Component.text(livingEntity.getName()))
                                                                    .build()
                                                    )
                                            );
                                return;
                            }
                        }
                        if (entities.size() != 1) continue;
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_DYE_FAILED_ENTITY
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
                                                    Message.COMMANDS_ADMIN_DYE_FAILED
                                                            .build()
                                            )
                                    );
                        return;
                    }

                    if (entities.size() == 1) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_DYE_SUCCESS_SINGLE
                                                            .arguments(Component.text(entities.iterator().next().getName()))
                                                            .build()
                                            )
                                    );
                    } else {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_DYE_SUCCESS_MULTIPLE
                                                            .arguments(Component.text(entities.size()))
                                                            .build()
                                            )
                                    );
                    }
                });
    }
}
