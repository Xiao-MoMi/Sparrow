package net.momirealms.sparrow.bukkit;

import com.mojang.brigadier.arguments.ArgumentType;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.Index;
import net.momirealms.sparrow.bukkit.command.feature.*;
import net.momirealms.sparrow.bukkit.command.parser.CustomEnchantmentParser;
import net.momirealms.sparrow.bukkit.command.preprocessor.MultipleEntitySelectorPostProcessor;
import net.momirealms.sparrow.bukkit.command.preprocessor.MultiplePlayerSelectorPostProcessor;
import net.momirealms.sparrow.common.command.AbstractSparrowCommandManager;
import net.momirealms.sparrow.common.command.CommandFeature;
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

    private static final List<CommandFeature<CommandSender>> FEATURES = List.of(
            new WorkbenchPlayerCommand(),
            new WorkbenchAdminCommand(),
            new HealPlayerCommand(),
            new HealAdminCommand(),
            new SuicidePlayerCommand(),
            new AnvilPlayerCommand(),
            new AnvilAdminCommand(),
            new EnderChestPlayerCommand(),
            new EnderChestAdminCommand(),
            new GrindStonePlayerCommand(),
            new GrindStoneAdminCommand(),
            new SmithingTablePlayerCommand(),
            new SmithingTableAdminCommand(),
            new StoneCutterPlayerCommand(),
            new StoneCutterAdminCommand(),
            new CartographyTablePlayerCommand(),
            new CartographyTableAdminCommand(),
            new LoomPlayerCommand(),
            new LoomAdminCommand(),
            new WorldAdminCommand(),
            new WorldPlayerCommand(),
            new SudoAdminCommand(),
            new FlyPlayerCommand(),
            new FlyAdminCommand(),
            new ActionBarAdminCommand(),
            new EnchantAdminCommand(),
            new ToastAdminCommand(),
            new FlySpeedAdminCommand(),
            new FlySpeedPlayerCommand(),
            new WalkSpeedAdminCommand(),
            new WalkSpeedPlayerCommand(),
            new FeedAdminCommand(),
            new FeedPlayerCommand(),
            new TitleAdminCommand(),
            new PatrolAdminCommand(),
            new BroadcastAdminCommand(),
            new ServerAdminCommand(),
            new ServerPlayerCommand(),
            new ColorAdminCommand(),
            new ReloadAdminCommand(),
            new DyePlayerCommand(),
            new DyeAdminCommand(),
            new TpOfflineAdminCommand(),
            new TpOfflinePlayerCommand(),
            new TopBlockAdminCommand(),
            new TopBlockPlayerCommand(),
            new BurnAdminCommand(),
            new ExtinguishAdminCommand(),
            new HeadAdminCommand(),
            new URLHeadAdminCommand(),
            new HeadPlayerCommand(),
            new MoreAdminCommand(),
            new MorePlayerCommand(),
            new TotemAdminCommand(),
            new DemoAdminCommand(),
            new DemoPlayerCommand(),
            new CreditsAdminCommand(),
            new CreditsPlayerCommand(),
            new EnchantmentTableAdminCommand(),
            new EnchantmentTablePlayerCommand(),
            new DistancePlayerCommand(),
            new HighlightPlayerCommand(),
            new HighlightAdminCommand()
    );

    private static final Index<String, CommandFeature<CommandSender>> INDEX = Index.create(CommandFeature::getFeatureID, FEATURES);

    @Override
    public Index<String, CommandFeature<CommandSender>> getFeatures() {
        return INDEX;
    }

    @Override
    protected Audience wrapAudience(CommandSender sender) {
        return ((SparrowBukkitSenderFactory) ((SparrowBukkitPlugin) plugin).getSenderFactory()).getAudience(sender);
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
