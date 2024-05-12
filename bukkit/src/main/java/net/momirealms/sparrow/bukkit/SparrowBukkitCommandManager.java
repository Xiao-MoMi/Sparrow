package net.momirealms.sparrow.bukkit;

import com.mojang.brigadier.arguments.ArgumentType;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.Index;
import net.momirealms.sparrow.bukkit.command.feature.*;
import net.momirealms.sparrow.bukkit.command.parser.CustomEnchantmentParser;
import net.momirealms.sparrow.bukkit.command.preprocessor.MultipleEntitySelectorPostProcessor;
import net.momirealms.sparrow.bukkit.command.preprocessor.MultiplePlayerSelectorPostProcessor;
import net.momirealms.sparrow.common.command.AbstractSparrowCommandManager;
import net.momirealms.sparrow.common.command.CommandFeature;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.sender.Sender;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.incendo.cloud.bukkit.internal.CraftBukkitReflection;
import org.incendo.cloud.bukkit.internal.RegistryReflection;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.parser.KeyedWorldParser;
import org.incendo.cloud.setting.ManagerSetting;

import java.util.List;

public final class SparrowBukkitCommandManager extends AbstractSparrowCommandManager<CommandSender> {

    private final List<CommandFeature<CommandSender>> FEATURES = List.of(
            new WorkbenchPlayerCommand(this),
            new WorkbenchAdminCommand(this),
            new HealPlayerCommand(this),
            new HealAdminCommand(this),
            new SuicidePlayerCommand(this),
            new AnvilPlayerCommand(this),
            new AnvilAdminCommand(this),
            new EnderChestPlayerCommand(this),
            new EnderChestAdminCommand(this),
            new GrindStonePlayerCommand(this),
            new GrindStoneAdminCommand(this),
            new SmithingTablePlayerCommand(this),
            new SmithingTableAdminCommand(this),
            new StoneCutterPlayerCommand(this),
            new StoneCutterAdminCommand(this),
            new CartographyTablePlayerCommand(this),
            new CartographyTableAdminCommand(this),
            new LoomPlayerCommand(this),
            new LoomAdminCommand(this),
            new WorldAdminCommand(this),
            new WorldPlayerCommand(this),
            new SudoAdminCommand(this),
            new FlyPlayerCommand(this),
            new FlyAdminCommand(this),
            new ActionBarAdminCommand(this),
            new EnchantAdminCommand(this),
            new ToastAdminCommand(this),
            new FlySpeedAdminCommand(this),
            new FlySpeedPlayerCommand(this),
            new WalkSpeedAdminCommand(this),
            new WalkSpeedPlayerCommand(this),
            new FeedAdminCommand(this),
            new FeedPlayerCommand(this),
            new TitleAdminCommand(this),
            new PatrolAdminCommand(this),
            new BroadcastAdminCommand(this),
            new ServerAdminCommand(this),
            new ServerPlayerCommand(this),
            new ColorAdminCommand(this),
            new ReloadAdminCommand(this),
            new DyePlayerCommand(this),
            new DyeAdminCommand(this),
            new TpOfflineAdminCommand(this),
            new TpOfflinePlayerCommand(this),
            new TopBlockAdminCommand(this),
            new TopBlockPlayerCommand(this),
            new BurnAdminCommand(this),
            new ExtinguishAdminCommand(this),
            new HeadAdminCommand(this),
            new URLHeadAdminCommand(this),
            new HeadPlayerCommand(this),
            new MoreAdminCommand(this),
            new MorePlayerCommand(this),
            new TotemAdminCommand(this),
            new DemoAdminCommand(this),
            new DemoPlayerCommand(this),
            new CreditsAdminCommand(this),
            new CreditsPlayerCommand(this),
            new EnchantmentTableAdminCommand(this),
            new EnchantmentTablePlayerCommand(this),
            new DistancePlayerCommand(this),
            new HighlightPlayerCommand(this),
            new HighlightAdminCommand(this)
    );

    private final Index<String, CommandFeature<CommandSender>> INDEX = Index.create(CommandFeature::getFeatureID, FEATURES);

    @Override
    public Index<String, CommandFeature<CommandSender>> getFeatures() {
        return INDEX;
    }

    @Override
    protected Audience wrapAudience(CommandSender sender) {
        return ((SparrowBukkitSenderFactory) ((SparrowBukkitPlugin) plugin).getSenderFactory()).getAudience(sender);
    }

    @Override
    protected Sender wrapSender(CommandSender sender) {
        return ((SparrowBukkitPlugin) plugin).getSenderFactory().wrap(sender);
    }

    public SparrowBukkitCommandManager(SparrowBukkitPlugin plugin) {
        super(plugin, new PaperCommandManager<>(
                plugin.getLoader(),
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        ));
        this.registerMappings();
        this.registerCommandPostProcessors();
    }

    private void registerCommandPostProcessors() {
        this.commandManager.registerCommandPostProcessor(MultiplePlayerSelectorPostProcessor.instance());
        this.commandManager.registerCommandPostProcessor(MultipleEntitySelectorPostProcessor.instance());
    }

    private void registerMappings() {
        final PaperCommandManager<CommandSender> manager = (PaperCommandManager<CommandSender>) getCommandManager();
        manager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        manager.parserRegistry().registerParser(CustomEnchantmentParser.enchantmentParser());
        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
            manager.brigadierManager().setNativeNumberSuggestions(true);
            BukkitBrigadierMapper<CommandSender> mapper = new BukkitBrigadierMapper<>(manager, manager.brigadierManager());
            switch (plugin.getBootstrap().getServerVersion()) {
                case "1.17.1", "1.18.1", "1.18.2", "1.19.1", "1.19.2" -> mapper.mapSimpleNMS(new TypeToken<CustomEnchantmentParser<CommandSender>>() {}, "item_enchantment");
                default -> mapper.mapNMS(
                        new TypeToken<CustomEnchantmentParser<CommandSender>>() {},
                        "resource_key",
                        type -> (ArgumentType<?>) type.getDeclaredConstructors()[0].newInstance(
                                RegistryReflection.registryKey(RegistryReflection.registryByName("enchantment"))
                        ),
                        true
                );
            }
            final Class<?> keyed = CraftBukkitReflection.findClass("org.bukkit.Keyed");
            if (keyed != null && keyed.isAssignableFrom(World.class)) {
                mapper.mapSimpleNMS(new TypeToken<KeyedWorldParser<CommandSender>>() {}, "resource_location", true);
            }
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }
    }
}
