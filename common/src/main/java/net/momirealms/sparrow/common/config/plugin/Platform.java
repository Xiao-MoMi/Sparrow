package net.momirealms.sparrow.common.config.plugin;

public enum Platform {
    BUKKIT("Bukkit"),
    BUNGEECORD("BungeeCord"),
    VELOCITY("Velocity");

    private final String platform;

    Platform(String platform) {
        this.platform = platform;
    }

    public String getPlatformName() {
        return platform;
    }
}