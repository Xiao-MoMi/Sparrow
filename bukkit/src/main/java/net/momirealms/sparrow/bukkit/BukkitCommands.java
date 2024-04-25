package net.momirealms.sparrow.bukkit;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.arguments.ArgumentType;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.leangen.geantyref.TypeToken;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.command.CommandConfig;
import net.momirealms.sparrow.bukkit.command.CommandFeature;
import net.momirealms.sparrow.bukkit.command.feature.*;
import net.momirealms.sparrow.bukkit.command.parser.CustomEnchantmentParser;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.incendo.cloud.bukkit.internal.CraftBukkitReflection;
import org.incendo.cloud.bukkit.internal.RegistryReflection;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.parser.KeyedWorldParser;

import java.util.HashSet;
import java.util.List;

public class BukkitCommands {

    private final HashSet<CommandComponent<? extends CommandSender>> registeredCommandComponents = new HashSet<>();

    private final List<CommandFeature> FEATURES = List.of(
            new WorkbenchPlayerCommand(),
            new WorkbenchAdminCommand(),
            new HatPlayerCommand(),
            new HatAdminCommand(),
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
            new SudoAdminCommand(),
            new FlyPlayerCommand(),
            new FlyAdminCommand(),
            new ActionBarAdminCommand(),
            new EnchantAdminCommand(),
            new ToastAdminCommand(),
            new FlySpeedAdminCommand(),
            new WalkSpeedAdminCommand(),
            new FeedAdminCommand(),
            new FeedPlayerCommand(),
            new TitleAdminCommand(),
            new PatrolAdminCommand()
    );

    private final SparrowBukkitPlugin plugin;
    private final PaperCommandManager<CommandSender> manager;

    public BukkitCommands(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        this.manager = new PaperCommandManager<>(
                plugin.getLoader(),
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );
        this.manager.parserRegistry().registerParser(CustomEnchantmentParser.enchantmentParser());
        if (this.manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.manager.registerBrigadier();
            this.manager.brigadierManager().setNativeNumberSuggestions(true);
            BukkitBrigadierMapper<CommandSender> mapper = new BukkitBrigadierMapper<>(this.manager, this.manager.brigadierManager());
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
        } else if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.manager.registerAsynchronousCompletions();
        }
    }

    public void unregisterCommandFeatures() {
        for (CommandComponent<? extends CommandSender> command : registeredCommandComponents) {
            this.manager.commandRegistrationHandler().unregisterRootCommand((CommandComponent<CommandSender>) command);
        }
        FEATURES.forEach(commandFeature -> {
            if (commandFeature instanceof AbstractCommand command) {
                command.unregisterRelatedFunctions();
            }
        });
    }

    public void registerCommandFeatures() {
        YamlDocument document = plugin.getConfigManager().loadConfig("commands.yml");
        FEATURES.forEach(feature -> feature.registerFeature(this, this.manager,
                Preconditions.checkNotNull(getCommandConfigFromDocument(document, feature.getFeatureID()), feature.getFeatureID() + " doesn't exist in commands.yml")));
    }

    private CommandConfig getCommandConfigFromDocument(YamlDocument document, String id) {
        Section section = document.getSection(id);
        if (section == null) return null;
        return new CommandConfig(
                section.getBoolean("enable", false),
                section.getStringList("usage"),
                section.getString("permission")
        );
    }

    public void addCommandComponent(CommandComponent<? extends CommandSender> command) {
        registeredCommandComponents.add(command);
    }
}
