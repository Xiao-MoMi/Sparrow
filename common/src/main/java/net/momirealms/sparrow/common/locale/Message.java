package net.momirealms.sparrow.common.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

public interface Message {

    TranslatableComponent.Builder COMMANDS_ADMIN_RELOAD_SUCCESS = Component.translatable().key("commands.admin.reload.success");
    TranslatableComponent.Builder COMMANDS_PLAYER_ANVIL_SUCCESS = Component.translatable().key("commands.player.anvil.success");
    TranslatableComponent.Builder COMMANDS_ADMIN_ANVIL_SUCCESS_SINGLE = Component.translatable().key("commands.admin.anvil.success.single");
    TranslatableComponent.Builder COMMANDS_ADMIN_ANVIL_SUCCESS_MULTIPLE = Component.translatable().key("commands.admin.anvil.success.multiple");
}
