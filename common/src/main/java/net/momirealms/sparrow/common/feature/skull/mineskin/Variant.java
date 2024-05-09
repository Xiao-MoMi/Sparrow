package net.momirealms.sparrow.common.feature.skull.mineskin;

public enum Variant {
    AUTO(""),
    CLASSIC("classic"),
    SLIM("slim");

    private final String name;

    Variant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
