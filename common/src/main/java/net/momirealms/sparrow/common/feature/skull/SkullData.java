package net.momirealms.sparrow.common.feature.skull;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface SkullData {

    String defaultOwner = "";
    String defaultTextureBase64 = "";
    UUID defaultUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @NotNull
    String getTextureBase64();

    @NotNull
    String getOwner();

    @NotNull
    UUID getUUID();

    static Builder builder() {
        return new SkullDataImpl.Builder();
    }

    interface Builder {

        SkullData.Builder textureBase64(final String base64);

        SkullData.Builder owner(final String owner);

        SkullData.Builder uuid(final UUID uuid);

        SkullData build();
    }
}
