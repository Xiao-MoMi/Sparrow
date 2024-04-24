package net.momirealms.sparrow.common.config;

import dev.dejvokep.boostedyaml.YamlDocument;

public interface ConfigManager {
    YamlDocument loadConfig(String fileName);
}
