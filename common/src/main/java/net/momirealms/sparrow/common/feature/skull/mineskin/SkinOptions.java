package net.momirealms.sparrow.common.feature.skull.mineskin;

import com.google.gson.JsonObject;

public class SkinOptions {

    private static final String URL_FORMAT = "name=%s&model=%s&visibility=%s";

    private final String name;
    private final Variant variant;
    private final Visibility visibility;

    private SkinOptions(String name, Variant variant, Visibility visibility) {
        this.name = name;
        this.variant = variant;
        this.visibility = visibility;
    }

    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (name != null && !name.isEmpty()) {
            json.addProperty("name", name);
        }
        if (variant != null && variant != Variant.AUTO) {
            json.addProperty("variant", variant.getName());
        }
        if (visibility != null) {
            json.addProperty("visibility", visibility.getCode());
        }
        return json;
    }

    public String getName() {
        return name;
    }

    public static SkinOptions create(String name, Variant variant, Visibility visibility) {
        return new SkinOptions(name, variant, visibility);
    }

    public static SkinOptions name(String name) {
        return new SkinOptions(name, Variant.AUTO, Visibility.PUBLIC);
    }

    public static SkinOptions none() {
        return new SkinOptions("", Variant.AUTO, Visibility.PUBLIC);
    }
}
