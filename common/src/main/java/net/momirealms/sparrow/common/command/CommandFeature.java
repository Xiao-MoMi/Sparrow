package net.momirealms.sparrow.common.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public interface CommandFeature<C> {

    Command<C> registerCommand(CommandManager<C> cloudCommandManager, Command.Builder<C> builder);

    String getFeatureID();

    void registerRelatedFunctions();

    void unregisterRelatedFunctions();
}
