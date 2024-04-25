package net.momirealms.sparrow.common.locale;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translatable;
import net.kyori.adventure.translation.TranslationRegistry;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import net.momirealms.sparrow.common.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationManager {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private final SparrowPlugin plugin;
    private final Set<Locale> installed = ConcurrentHashMap.newKeySet();
    private TranslationRegistry registry;

    private final Path translationsDirectory;
    private final Path repositoryTranslationsDirectory;
    private final Path customTranslationsDirectory;

    public TranslationManager(SparrowPlugin plugin) {
        this.plugin = plugin;
        this.translationsDirectory = this.plugin.getBootstrap().getConfigDirectory().resolve("translations");
        this.repositoryTranslationsDirectory = this.translationsDirectory.resolve("repository");
        this.customTranslationsDirectory = this.translationsDirectory.resolve("custom");

        try {
            FileUtils.createDirectoriesIfNotExists(this.repositoryTranslationsDirectory);
            FileUtils.createDirectoriesIfNotExists(this.customTranslationsDirectory);
        } catch (IOException ignored) {
        }
    }

    public void reload() {
        // remove any previous registry
        if (this.registry != null) {
            GlobalTranslator.translator().removeSource(this.registry);
            this.installed.clear();
        }

        // create a translation registry
        this.registry = TranslationRegistry.create(Key.key("sparrow", "main"));
        this.registry.defaultLocale(DEFAULT_LOCALE);

        // register it to the global source, so our translations can be picked up by adventure-platform
        GlobalTranslator.translator().addSource(this.registry);
    }
}
