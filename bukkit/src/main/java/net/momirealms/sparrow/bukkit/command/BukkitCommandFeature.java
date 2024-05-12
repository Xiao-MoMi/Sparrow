package net.momirealms.sparrow.bukkit.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.sender.SenderFactory;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.bukkit.data.Selector;

import java.util.Collection;

public abstract class BukkitCommandFeature<C extends CommandSender> extends AbstractCommandFeature<C> {

    public BukkitCommandFeature(SparrowCommandManager<C> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SenderFactory<?, C> getSenderFactory() {
        return (SenderFactory<?, C>) SparrowBukkitPlugin.getInstance().getSenderFactory();
    }

    public Pair<TranslatableComponent.Builder, Component> resolveSelector(Selector<? extends Entity> selector, TranslatableComponent.Builder single, TranslatableComponent.Builder multiple) {
        Collection<? extends Entity> entities = selector.values();
        if (entities.size() == 1) {
            return Pair.of(single, Component.text(entities.iterator().next().getName()));
        } else {
            return Pair.of(multiple, Component.text(entities.size()));
        }
    }

    public Pair<TranslatableComponent.Builder, Component> resolveSelector(Collection<? extends Entity> selector, TranslatableComponent.Builder single, TranslatableComponent.Builder multiple) {
        if (selector.size() == 1) {
            return Pair.of(single, Component.text(selector.iterator().next().getName()));
        } else {
            return Pair.of(multiple, Component.text(selector.size()));
        }
    }
}
